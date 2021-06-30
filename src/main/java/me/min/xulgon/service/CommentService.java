package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.mapper.CommentMapper;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
   private final PostRepository postRepository;

   private final AuthenticationService authService;
   private final ContentRepository contentRepository;
   private final CommentMapper commentMapper;
   private final BlockService blockService;
   private final PhotoService photoService;

   public CommentResponse get(Long id) {
      return commentRepository.findById(id)
            .map(commentMapper::toDto)
            .orElseThrow(RuntimeException::new);
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
      User loggedInUser = authService.getLoggedInUser();
      if (!comment.getParentContent().getUser().equals(loggedInUser)) {

         Notification notification = Notification.builder()
               .actor(loggedInUser)
               .createdAt(Instant.now())
               .recipient(comment.getParentContent().getUser())
               .isRead(false)
               .content(comment)
               .page(comment.getPage())
               .type(NotificationType.COMMENT)
               .build();

         notification = notificationRepository.save(notification);
         simpMessagingTemplate.convertAndSendToUser(comment.getParentContent().getUser().getUsername(),
               "/queue/notification",
               notificationMapper.toDto(notification));
      }
      return commentMapper.toDto(comment);
   }

   private void modifyParentsCommentCount(Comment comment) {
      Content parent = comment.getParentContent();
      switch (parent.getType()) {
         case POST:
         case PHOTO:
            parent.setCommentCount(parent.getCommentCount()+1);
            contentService.save(parent);
            break;
         case COMMENT:
            parent.setCommentCount(parent.getCommentCount()+1);
            contentService.save(parent);
            modifyParentsCommentCount((Comment) parent);
            break;
         default:
            break;
      }
   }

   public void deleteComment(Long id) {
      commentRepository.deleteById(id);
   }

   @Transactional(readOnly = true)
   public List<CommentResponse> getCommentsByContent(Long contentId) {
      Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
      return commentRepository.findAllByParentContent(content).stream()
            .filter(blockService::filter)
            .map(commentMapper::toDto)
            .collect(Collectors.toList());

   }
}
