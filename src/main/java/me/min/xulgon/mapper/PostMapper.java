package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.service.AuthenticationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
      imports = {Instant.class, DateTimeFormatter.class, LocalDateTime.class})
@Slf4j
public abstract class PostMapper {

   @Autowired
   AuthenticationService authenticationService;
   @Autowired
   PostMapper postMapper;
   @Autowired
   PostRepository postRepository;
   @Autowired
   PageRepository pageRepository;
   @Autowired
   PhotoMapper photoMapper;

   @Mapping(target = "id"        , ignore = true)
   @Mapping(target = "type"      , constant = "POST")
   @Mapping(target = "createdAt" , expression = "java(Instant.now())")
   @Mapping(target = "body"      , expression = "java(postRequest.getBody())")
   @Mapping(target = "comments"  , expression = "java(new java.util.LinkedList<>())")
   @Mapping(target = "reactions" , expression = "java(new java.util.LinkedList<>())")
   @Mapping(target = "photos"    , expression = "java(new java.util.LinkedList<>())")
   @Mapping(target = "privacy"   , expression = "java(postRequest.getPrivacy())")
   @Mapping(target = "page"      , expression = "java(getPage(postRequest.getPageId()))")
   @Mapping(target = "user"      , expression = "java(authenticationService.getLoggedInUser())")
   @Mapping(target = "sharedPost", expression = "java(getSharedPost(postRequest.getSharedPostId()))")
   public abstract Post map(PostRequest postRequest);

   @Mapping(target = "pageId"       , expression = "java(post.getPage().getId())")
   @Mapping(target = "userId"       , expression = "java(post.getUser().getId())")
   @Mapping(target = "username"     , expression = "java(getUsername(post))")
   @Mapping(target = "reactionCount", expression = "java(post.getReactions().size())")
   @Mapping(target = "commentCount" , expression = "java(post.getComments().size())")
   @Mapping(target = "shareCount"   , constant = "0")
   @Mapping(target = "createdAt"    , expression = "java(toDate(post.getCreatedAt()))")
   @Mapping(target = "isReacted"    , expression = "java(isReacted(post))")
   @Mapping(target = "photoCount"   , expression = "java(post.getPhotos().size())")
   @Mapping(target = "sharedPost"   , expression = "java(postMapper.toDto(post.getSharedPost()))")
   @Mapping(target = "photos"       , expression = "java(getPhotoResponses(post.getPhotos()))")
   public abstract PostResponse toDto(Post post);

   String getUsername(Content content) {
      return content.getUser().getLastName() + " " + content.getUser().getFirstName();
   }

   List<PhotoResponse> getPhotoResponses(List<Photo> photos) {
      var photoDto = photos.stream()
            .map(photoMapper::toDto)
            .collect(Collectors.toList());
      return photoDto;
   }

   String toDate(Instant instant) {
      var date = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(7));
      return DateTimeFormatter.ofPattern("d 'tháng' M 'lúc' HH:mm").format(date);
   }

   boolean isReacted(Content content) {
      User user = authenticationService.getLoggedInUser();

      return content.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

   Page getPage(Long pageId) {
      return pageRepository.findById(pageId)
            .orElseThrow(() -> new RuntimeException("Page not found"));
   }

   Post getSharedPost(Long postId) {
      return postId == null ? null : postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
   }

}
