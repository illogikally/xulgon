package me.min.xulgon.mapper;

import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.service.AuthenticationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring",
      imports = {DateTimeFormatter.class, LocalDateTime.class})
public abstract class PostMapper {

   @Autowired
   AuthenticationService authenticationService;

   @Mapping(target = "pageId", expression = "java(post.getPage().getId())")
   @Mapping(target = "userId", expression = "java(post.getUser().getId())")
   @Mapping(target = "username",
         expression = "java(post.getUser().getLastName() + \" \" + post.getUser().getFirstName())")
   @Mapping(target = "reactionCount", expression = "java(post.getReactions().size())")
   @Mapping(target = "commentCount", expression = "java(post.getComments().size())")
   @Mapping(target = "shareCount", constant = "0")
   @Mapping(target = "createdAt", expression = "java(toDate(post.getCreatedAt()))")
   @Mapping(target = "isReacted", expression = "java(isReacted(post))")
   @Mapping(target = "photoCount", expression = "java(post.getPhotos().size())")
   public abstract PostResponse toDto(Post post);

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

}
