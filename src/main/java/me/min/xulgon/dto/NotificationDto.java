package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.ContentType;
import me.min.xulgon.model.NotificationType;

@Data
@Builder
public class NotificationDto {
   private Long id;
   private NotificationType type;
   private String text;
   private Boolean isRead;
   private Long targetId;
   private ContentType targetType;
   private String pageName;
   private Long postId;
   private String actorName;
   private String createdAgo;
   private String actorAvatarUrl;
}
