package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.PageType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentNotificationDto {
   private Long id;
   private String createdAgo;
   private NotificationType type;
   private Long actorId;
   private String actorFullName;
   private String actorAvatarUrl;
   private Long commentId;
   private String commentBody;
   private Long postId;
   private String postBody;
   private Long pageId;
   private PageType pageType;
}
