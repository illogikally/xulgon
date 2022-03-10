package me.min.xulgon.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import me.min.xulgon.model.Comment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MappingUtil {

   public static String getCreatedAgo(Instant instant) {
      return TimeAgo.using(instant.toEpochMilli())
            .replace("yesterday", "hôm qua")
            .replace("just now", "vừa tức thì")
            .replaceAll("about|ago", "")
            .replaceAll("(?<=^| )(an|one|a) ", "1 ")
            .replaceAll("hours?", "giờ")
            .replaceAll("seconds?", "giây")
            .replaceAll("minutes?", "phút")
            .replaceAll("days?", " ngày")
            .replaceAll("years?", "năm")
            .replaceAll("months?", "tháng");
   }

   public static String getCreatedAt(Instant instant) {
      LocalDateTime date = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(7));
      return DateTimeFormatter.ofPattern("d 'tháng' M 'lúc' HH:mm").format(date);
   }
}
