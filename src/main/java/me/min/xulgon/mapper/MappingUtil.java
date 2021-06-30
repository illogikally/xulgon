package me.min.xulgon.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import me.min.xulgon.model.Comment;
import org.springframework.stereotype.Service;

import java.time.Instant;

public class MappingUtil {

   public static String getCreatedAgo(Instant instant) {
      return TimeAgo.using(instant.toEpochMilli())
            .replace("yesterday", "hôm qua")
            .replace("just now", "vừa tức thì")
            .replaceAll("(about|ago)", "")
            .replaceAll("(an|one)", "1")
            .replaceAll("hours?", "giờ")
            .replaceAll("seconds?", "giây")
            .replaceAll("minutes?", "phút")
            .replaceAll("days?", " ngày")
            .replaceAll("years?", "năm")
            .replaceAll("months?", "tháng");
   }
}
