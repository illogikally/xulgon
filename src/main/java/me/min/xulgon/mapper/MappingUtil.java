package me.min.xulgon.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import me.min.xulgon.model.Comment;

import java.time.Instant;

public class MappingUtil {

   public static String getCreatedAgo(Instant instant) {
      return TimeAgo.using(instant.toEpochMilli())
            .replace("yesterday", "hôm qua")
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
