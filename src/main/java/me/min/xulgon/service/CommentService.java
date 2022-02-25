package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.mapper.CommentMapper;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.ContextNotEmptyException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CommentService {
   private final CommentRepository commentRepository;
   private final NotificationMapper notificationMapper;
   private final ContentService contentService;
   private final SimpMessagingTemplate simpMessagingTemplate;
   private final NotificationRepository notificationRepository;
   private final NotificationSubjectRepository notificationSubjectRepository;
   private final AuthenticationService authService;
   private final ContentRepository contentRepository;
   private final CommentMapper commentMapper;
   private final BlockService blockService;
   private final PhotoService photoService;

   public CommentResponse get(Long id) {
      return commentRepository.findById(id)
            .map(commentMapper::toDto)
            .orElseThrow(ContentNotFoundException::new);
   }

   public CommentResponse save(CommentRequest commentRequest,
                               MultipartFile photoMultipart) {
      Comment comment = commentRepository.save(commentMapper.map(commentRequest));
      if (!photoMultipart.isEmpty()) {
         PhotoRequest photoRequest = new PhotoRequest();
         photoRequest.setParentId(comment.getId());
         photoRequest.setPrivacy(Privacy.PUBLIC);
         Photo photo = photoService.save(photoRequest, photoMultipart);
         comment.setPhotos(List.of(photo));
      }

      modifyParentsCommentCount(comment);
      createCommentNotification(comment);
      return commentMapper.toDto(comment);
   }

   private void createCommentNotification(Comment comment) {
      User principal = authService.getPrincipal();
      Content rootContent = comment.getRootContent();
      if (!rootContent.getUser().equals(principal)) {
         Notification notification = Notification
               .builder()
               .createdAt(Instant.now())
               .actor(principal)
               .actorContent(comment)
               .build();

         notification = notificationRepository.save(notification);
         NotificationSubject subject =
               notificationSubjectRepository.findBySubjectContentAndType(comment.getParentContent(), NotificationType.COMMENT)
               .orElseGet(() ->
                  NotificationSubject
                        .builder()
                        .type(NotificationType.COMMENT)
                        .actorCount(0)
                        .rootContent(rootContent)
                        .page(rootContent.getPage())
                        .subjectContent(comment.getParentContent())
                        .notifications(List.of())
                        .recipient(rootContent.getUser())
                        .build()
               );

         subject.setLatestNotification(notification);
         if (subject.getNotifications()
               .stream()
               .noneMatch(n -> n.getActor().equals(principal))
         ) {
            subject.setActorCount(subject.getActorCount() + 1);
         }
         subject.setLatestCreatedAt(notification.getCreatedAt());
         subject = notificationSubjectRepository.save(subject);
         subject.setIsRead(false);
         notification.setSubject(subject);
         notificationRepository.save(notification);
         simpMessagingTemplate.convertAndSendToUser(
               comment.getParentContent().getUser().getUsername(),
               "/queue/notification",
               notificationMapper.toDto(subject));
      }

   }

   private void modifyParentsCommentCount(Comment comment) {
      Content parent = comment.getParentContent();
      parent.setCommentCount(parent.getCommentCount() + 1);
      contentRepository.save(parent);
   }

   public void deleteComment(Long id) {
      commentRepository.deleteById(id);
   }

   @Transactional(readOnly = true)
   public OffsetResponse<CommentResponse> getCommentsByContent(Long contentId,
                                                               Pageable pageable) {
      Content content = contentRepository.findById(contentId)
            .orElseThrow(ContentNotFoundException::new);
      var size = pageable.getPageSize();
      pageable = new OffsetRequest(size + 1, pageable.getOffset());

      var commentResponses =
            commentRepository.findAllByParentContent(content, pageable)
            .stream()
            .filter(blockService::filter)
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
      Boolean hasNext = commentResponses.size() > size;

      return OffsetResponse
            .<CommentResponse>builder()
            .hasNext(hasNext)
            .offset(pageable.getOffset())
            .size(size)
            .data(commentResponses.stream().limit(size).collect(Collectors.toList()))
            .build();
   }
}
