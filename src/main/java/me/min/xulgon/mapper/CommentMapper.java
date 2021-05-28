package me.min.xulgon.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.service.AuthenticationService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.LinkedList;

@Mapper(componentModel = "spring",
      imports = {LinkedList.class, TimeAgo.class, Instant.class, ContentType.class})
public abstract class CommentMapper {

   @Autowired
   AuthenticationService authenticationService;

   @Mapping(target = "id", ignore = true)
   @Mapping(target = "type", constant = "COMMENT")
   @Mapping(target = "createdAt", expression = "java(Instant.now())")
   @Mapping(target = "body", source = "commentRequest.body")
   @Mapping(target = "parent", source = "parent")
   @Mapping(target = "comments", expression = "java(new LinkedList<>())")
   @Mapping(target = "reactions", expression = "java(new LinkedList<>())")
   public abstract Comment map(CommentRequest commentRequest,
                               Page page,
                               User user,
                               Content parent);

   @Mapping(target = "userId", expression = "java(comment.getUser().getId())")
   @Mapping(target = "username",
         expression = "java(comment.getUser().getLastName() + \" \" + comment.getUser().getFirstName())")
   @Mapping(target = "avatarUrl", constant = "")
   @Mapping(target = "parentId", expression = "java(comment.getParent().getId())")
   @Mapping(target = "createdAgo", expression = "java(toVietnamese(TimeAgo.using(comment.getCreatedAt().toEpochMilli())))")
   @Mapping(target = "reactionCount", expression = "java(comment.getReactions().size())")
   @Mapping(target = "replyCount", expression = "java(comment.getComments().size())")
   @Mapping(target = "parentType", expression = "java(comment.getParent().getType().toString())")
   @Mapping(target = "isReacted", expression = "java(isReacted(comment))")
   public abstract CommentResponse toDto(Comment comment);

   boolean isReacted(Comment comment) {
      User user = authenticationService.getLoggedInUser();

      return comment.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

   String toVietnamese(String timeAgo) {
      return timeAgo.replace("yesterday", "hôm qua")
            .replace("one", "một")
            .replace("just now", "vừa tức thì")
            .replace("about", "khoảng")
            .replace("an", "một")
            .replace("ago", "trước")
            .replaceAll("hours?", "giờ")
            .replaceAll("seconds?", "giây")
            .replaceAll("minutes?", "phút")
            .replaceAll("days?", " ngày")
            .replaceAll("years?", "năm")
            .replaceAll("months?", "tháng");
   }

}
