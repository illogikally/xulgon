package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.PageType;

@Data
@Builder
public class NotificationDto {
   private Long id;
   private String createdAgo;
   private NotificationType type;
   private Long actorId;
   private Integer actorCount;
   private String actorFullName;
   private String actorAvatarUrl;
   private Long actorContentId;
   private String actorContentBody;
   private Long recipientContentId;
   private String recipientContentBody;
   private Boolean isRead;
   private Long pageId;
   private PageType pageType;
}
