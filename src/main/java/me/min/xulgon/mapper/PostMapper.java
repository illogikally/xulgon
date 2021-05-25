package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", imports = {DateTimeFormatter.class, LocalDateTime.class})
@AllArgsConstructor
public abstract class PostMapper {


   @Mapping(target = "pageId", expression = "java(post.getPage().getId())")
   @Mapping(target = "userId", expression = "java(post.getUser().getId())")
   @Mapping(target = "username",
         expression = "java(post.getUser().getLastName() + \" \" + post.getUser().getFirstName())")
   @Mapping(target = "reactionCount", expression = "java(post.getReactions().size())")
   @Mapping(target = "commentCount", expression = "java(post.getComments().size())")
   @Mapping(target = "shareCount", constant = "0")
   @Mapping(target = "createdAt", expression = "java(toDate(post.getCreatedAt()))")
   public abstract PostResponse toDto(Post post);

   String toDate(Instant instant) {
      var date = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(7));
      return DateTimeFormatter.ofPattern("d 'tháng' M 'lúc' H:mm").format(date);
   }

   String vietHoa(String timeAgo) {
      return timeAgo.replaceAll("hours?", "giờ")
            .replaceAll("seconds?", "giây")
            .replaceAll("minutes?", "phút")
            .replaceAll("days?", "ngày")
            .replace("ago", "trước");
   }
}
