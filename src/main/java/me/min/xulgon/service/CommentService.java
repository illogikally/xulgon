package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.mapper.CommentMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.CommentRepository;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.PhotoSetRepository;
import me.min.xulgon.util.OffsetRequest;
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
   private final ContentRepository contentRepository;
   private final CommentMapper commentMapper;
   private final BlockService blockService;
   private final PhotoService photoService;
   private PhotoSetPhotoService photoSetPhotoService;
   private PhotoSetRepository photoSetRepository;
   private NotificationService notificationService;
   private FollowRepository followRepository;
   private FollowService followService;
   private WebSocketService webSocketService;

   public CommentResponse get(Long id) {
      return commentRepository.findById(id)
            .map(commentMapper::toDto)
            .orElseThrow(ContentNotFoundException::new);
   }

   public CommentResponse save(CommentRequest commentRequest,
                               MultipartFile photoMultipart) {
      Comment comment = commentRepository.save(commentMapper.map(commentRequest));
      PhotoSet set = photoSetRepository.save(PhotoSet.generate(SetType.COMMENT));
      comment.setPhotoSet(set);
      comment.setPhotos(List.of());
      if (!photoMultipart.isEmpty()) {
         PhotoRequest photoRequest = new PhotoRequest();
         photoRequest.setParentId(comment.getId());
         photoRequest.setPrivacy(Privacy.PUBLIC);
         Photo photo = photoService.save(photoRequest, photoMultipart);
         photoSetPhotoService.bulkInsertUnique(set, List.of(photo));
         comment.setPhotos(List.of(photo));
      }

      modifyParentsCommentCount(comment);
      followTargetContent(comment);
      createCommentNotification(comment);
      followService.followContent(comment.getId());
      var commentResponse = commentMapper.toDto(comment);
      webSocketService.send(
            comment.getParentContent().getId(),
            WebSocketContentType.COMMENT,
            commentResponse,
            "/topic/comment"
      );
      return commentResponse;
   }

   private void followTargetContent(Comment comment) {
      boolean isFollow =
            followRepository.findByFollowerAndContent(comment.getUser(), comment.getParentContent())
                  .isPresent();

      if (!isFollow) {
         followRepository.save(
               Follow.builder()
                     .follower(comment.getUser())
                     .content(comment.getParentContent())
                     .createdAt(Instant.now())
                     .build()
         );
      }
   }

   private void createCommentNotification(Comment comment) {

      comment.getParentContent().getFollows().forEach(follow -> {
         notificationService.createNotification(
               comment.getUser(),
               comment.getParentContent(),
               comment.getRootContent(),
               comment,
               comment.getPage(),
               follow.getFollower(),
               NotificationType.COMMENT
         );
      });
   }

   private void modifyParentsCommentCount(Comment comment) {
      Content parent = comment.getParentContent();
      parent.setCommentCount(parent.getCommentCount() + 1);
      contentRepository.save(parent);
   }

   @Transactional(readOnly = true)
   public OffsetResponse<CommentResponse> getCommentsByContent(Long contentId,
                                                               OffsetRequest pageable) {
      Content content = contentRepository.findById(contentId)
            .orElseThrow(ContentNotFoundException::new);
      var size = pageable.getPageSize();
      pageable = pageable.sizePlusOne();

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
