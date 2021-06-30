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
   private String contentBody;
   private Boolean isRead;
   private Long contentId;
   private ContentType contentType;
   private String pageName;
   private Long postId;
   private String actorName;
   private String createdAgo;
   private String actorAvatar;
}
