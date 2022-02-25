package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.ContentType;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.PageType;

@Data
@Builder
public class NotificationDto {
   private Long id;
   private String createdAgo;
   private NotificationType type;
   private Boolean isRead;

   private Long actorId;
   private Integer actorCount;
   private String actorFullName;
   private String actorAvatarUrl;
   private Long actorContentId;
   private String actorContentBody;

   private Long recipientContentId;
   private String recipientContentBody;
   private ContentType recipientContentType;

   private Long rootContentId;
   private ContentType rootContentType;

   private Long pageId;
   private PageType pageType;
}
