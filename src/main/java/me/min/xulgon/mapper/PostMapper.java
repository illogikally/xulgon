package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostMapper {

   private final PhotoMapper photoMapper;
   private final AuthenticationService authenticationService;
   private final PostRepository postRepository;
   private final PageRepository pageRepository;
   private final CommentMapper commentMapper;
   private final UserMapper userMapper;
   private final PhotoViewMapper photoViewMapper;

   public Post map(PostRequest postRequest) {
      if (postRequest == null) return null;

      return Post.builder()
            .type(ContentType.POST)
            .createdAt(Instant.now())
            .commentCount(0)
            .reactionCount(0)
            .body(postRequest.getBody())
            .comments(new LinkedList<>())
            .reactions(new LinkedList<>())
            .photos(new LinkedList<>())
            .privacy(postRequest.getPrivacy())
            .page(getPage(postRequest.getPageId()))
            .user(authenticationService.getPrincipal())
            .sharedPost(getSharedPost(postRequest.getSharedPostId()))
            .build();

   }


   public PostResponse toDto(Post post) {
      if (post == null) return null;

      return PostResponse.builder()
            .id(post.getId())
            .pageName(post.getPage().getName())
            .pageId(post.getPage().getId())
            .pageType(post.getPage().getType())
            .reactionCount(post.getReactionCount())
            .commentCount(post.getCommentCount())
            .user(userMapper.toDto(post.getUser()))
//            .comments(getFirstTwo(post))
            .body(post.getBody())
            .shareCount(0)
            .privacy(post.getPrivacy())
            .createdAt(toDate(post.getCreatedAt()))
            .isReacted(isReacted(post))
            .sharedPost(this.toDto(post.getSharedPost()))
            .photos(getPhotoResponses(post.getPhotos()))
            .build();
   }

   private List<CommentResponse> getFirstTwo(Post post) {
      return post.getComments().stream()
            .limit(2)
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
   }

   private Integer getCommentCount(Content content, Integer count) {
      for (Comment comment: content.getComments()) {
         count = getCommentCount(comment, count+1);
      }
      return count;
   }

   private String getUsername(Content content) {
      return content.getUser().getFullName();
   }

   private List<PhotoResponse> getPhotoResponses(List<Photo> photos) {
      return photos.stream()
            .limit(4)
            .map(photoMapper::toDto)
            .collect(Collectors.toList());
   }

   private String toDate(Instant instant) {
      LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(7));
      return DateTimeFormatter.ofPattern("d 'tháng' M 'lúc' HH:mm").format(date);
   }

   private boolean isReacted(Content content) {
      User user = authenticationService.getPrincipal();

      return content.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

   private Page getPage(Long pageId) {
      return pageRepository.findById(pageId)
            .orElseThrow(() -> new RuntimeException("Page not found"));
   }

   private Post getSharedPost(Long postId) {
      return postId == null ? null : postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
   }

}
