package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.ContentType;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.PageType;

@Data
@Builder
public class ReplyNotificationDto {
   private Long notificationId;
   private String commentText;
   private NotificationType notificationType;
   private Boolean isRead;
   private Long targetId;
   private ContentType targetType;
   private String pageName;
   private PageType pageType;
   private Long postId;
   private String actorName;
   private String createdAgo;
   private String actorAvatarUrl;
}
