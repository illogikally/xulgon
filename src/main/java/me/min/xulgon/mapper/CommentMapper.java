package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedList;

@Service
@AllArgsConstructor
public class CommentMapper {

   private final AuthenticationService authenticationService;
   private final PageRepository pageRepository;
   private final UserMapper userMapper;
   private final ContentRepository contentRepository;
   private final PhotoViewMapper photoViewMapper;


   public Comment map(CommentRequest commentRequest) {
      if (commentRequest == null) return null;

      return Comment.builder()
            .parent(getParent(commentRequest))
            .type(ContentType.COMMENT)
            .user(authenticationService.getLoggedInUser())
            .createdAt(Instant.now())
            .page(getPage(commentRequest))
            .body(commentRequest.getBody())
            .comments(new LinkedList<>())
            .reactions(new LinkedList<>())
            .build();

   }

   public CommentResponse toDto(Comment comment) {
      if (comment == null) return null;

      return CommentResponse.builder()
            .id(comment.getId())
            .parentType(comment.getParent().getType())
            .body(comment.getBody())
            .isReacted(isReacted(comment))
            .user(userMapper.toDto(comment.getUser()))
            .photo(getPhoto(comment))
            .parentId(comment.getParent().getId())
            .createdAgo(MappingUtil.getCreatedAgo(comment.getCreatedAt()))
            .reactionCount(comment.getReactions().size())
            .replyCount(comment.getComments().size())
            .build();
   }

   private PhotoViewResponse getPhoto(Comment comment) {
      if (comment.getPhotos() == null) return null;
      if (comment.getPhotos().isEmpty()) return null;
      return photoViewMapper.toDto(comment.getPhotos().get(0));
   }

   private String getUsername(Comment comment) {
      return comment.getUser().getLastName() + " " + comment.getUser().getFirstName();
   }

   private Page getPage(CommentRequest commentRequest) {
      return pageRepository.findById(getParent(commentRequest).getPage().getId())
            .orElseThrow(() -> new RuntimeException("Page not found"));
   }

   private Content getParent(CommentRequest commentRequest) {
      return contentRepository.findById(commentRequest.getParentId())
            .orElseThrow(() -> new RuntimeException("Content not found"));
   }

   boolean isReacted(Comment comment) {
      User user = authenticationService.getLoggedInUser();

      return comment.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

}
