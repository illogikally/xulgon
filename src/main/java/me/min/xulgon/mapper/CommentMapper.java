package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedList;

@Service
@AllArgsConstructor
public class CommentMapper {

   private final AuthenticationService authenticationService;
   private final PostRepository postRepository;
   private final PageRepository pageRepository;
   private final UserMapper userMapper;
   private final PhotoMapper photoMapper;
   private final ContentRepository contentRepository;


   public Comment map(CommentRequest commentRequest) {
      if (commentRequest == null) return null;

      return Comment.builder()
            .parentContent(getParent(commentRequest))
            .type(ContentType.COMMENT)
            .commentCount(0)
            .reactionCount(0)
            .user(authenticationService.getPrincipal())
            .createdAt(Instant.now())
            .rootContent(getRootContent(commentRequest))
            .page(getPage(commentRequest))
            .body(commentRequest.getBody())
            .comments(new LinkedList<>())
            .reactions(new LinkedList<>())
            .build();
   }

   private Content getRootContent(CommentRequest commentRequest) {
      return contentRepository.findById(commentRequest.getRootContentId())
            .orElseThrow(ContentNotFoundException::new);
   }

   public CommentResponse toDto(Comment comment) {
      if (comment == null) return null;


      return CommentResponse.builder()
            .id(comment.getId())
            .parentType(comment.getParentContent().getType())
            .body(comment.getBody())
            .isReacted(isReacted(comment))

            .rootContentId(comment.getRootContent().getId())
            .rootContentType(comment.getRootContent().getType())

            .parentId(comment.getParentContent().getId())

            .user(userMapper.toDto(comment.getUser()))
            .photo(getPhoto(comment))
            .createdAgo(MappingUtil.getCreatedAgo(comment.getCreatedAt()))
            .reactionCount(comment.getReactionCount())
            .replyCount(comment.getCommentCount())
            .build();
   }

   private PhotoViewResponse getPhoto(Comment comment) {
      if (comment.getPhotos() == null) return null;
      if (comment.getPhotos().isEmpty()) return null;
      return photoMapper.toPhotoViewResponse(comment.getPhotos().get(0));
   }

   private String getUsername(Comment comment) {
      return comment.getUser().getFullName();
   }

   private Page getPage(CommentRequest commentRequest) {
      return pageRepository.findById(getParent(commentRequest).getPage().getId())
            .orElseThrow(PageNotFoundException::new);
   }

   private Content getParent(CommentRequest commentRequest) {
      return contentRepository.findById(commentRequest.getParentId())
            .orElseThrow(ContentNotFoundException::new);
   }

   boolean isReacted(Comment comment) {
      User user = authenticationService.getPrincipal();

      return comment.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

}
