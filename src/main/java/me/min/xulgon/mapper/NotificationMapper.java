package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentNotificationDto;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.model.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationMapper {

   public NotificationDto toDto(Notification notification) {
      return NotificationDto
            .builder()
            .actorContentBody(notification.getActorContent().getBody())
            .actorAvatarUrl(notification.getActor().getUserPage().getAvatar().getUrl())
            .actorFullName(notification.getActor().getFullName())
            .pageId(notification.getPage().getId())
            .pageType(notification.getPage().getType())
            .actorContentId(notification.getActorContent().getId())
            .id(notification.getId())
            .createdAgo(MappingUtil.getCreatedAgo(notification.getCreatedAt()))
            .actorId(notification.getActor().getId())
            .recipientContentId(notification.getRecipientContent().getId())
            .recipientContentBody(notification.getRecipientContent().getBody())
            .type(notification.getType())
            .build();
   }
//   public NotificationDto toDto(Notification notification) {
//      return NotificationDto.builder()
//            .id(notification.getId())
//            .type(notification.getType())
//            .createdAgo(MappingUtil.getCreatedAgo(notification.getCreatedAt()))
//            .contentType(notification.getContent().getType())
//            .contentBody(notification.getContent().getBody())
//            .isRead(notification.getIsRead())
//            .actorAvatar(notification.getActor().getUserPage().getAvatar().getUrl())
//            .contentId(notification.getContent().getId())
//            .pageName(notification.getPage().getName())
//            .actorName(notification.getActor().getFullName())
//            .postId(getPostId(notification))
//            .build();
//   }
//
//   private Long getPostId(Notification notif) {
//      if (notif.getContent().getType().equals(ContentType.COMMENT))
//         return ((Comment) notif.getContent()).getPost().getId();
//      return notif.getContent().getId();
//   }
}
