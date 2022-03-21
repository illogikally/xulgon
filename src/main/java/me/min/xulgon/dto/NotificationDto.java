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
   private Boolean isRead;
   private Boolean isPreviousRead;

   private Integer actorCount;

   private UserBasicDto actor;

   private NotificationContentDto targetContent;
   private NotificationContentDto actorContent;
   private NotificationContentDto targetContentParent;
   private NotificationContentDto rootContent;

   private Long pageId;
   private PageType pageType;
   private String pageName;
}
