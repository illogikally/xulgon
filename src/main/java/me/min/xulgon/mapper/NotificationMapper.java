package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentNotificationDto;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.model.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationMapper {

   public NotificationDto toDto(NotificationSubject notification) {
      Notification latest = notification.getLatestNotification();
      return NotificationDto
            .builder()
            .actorContentBody(notification.getSubjectContent().getBody())
            .actorAvatarUrl(latest.getActor().getUserPage().getAvatar().getUrl())
            .actorFullName(latest.getActor().getFullName())
            .pageId(notification.getPage().getId())
            .pageType(notification.getPage().getType())
            .actorContentId(latest.getActorContent().getId())
            .actorCount(notification.getActorCount())
            .id(notification.getId())
            .isRead(notification.getIsRead())
            .createdAgo(MappingUtil.getCreatedAgo(notification.getLatestCreatedAt()))
            .actorId(latest.getActor().getId())
            .recipientContentId(notification.getSubjectContent().getId())
            .recipientContentBody(notification.getSubjectContent().getBody())
            .type(notification.getType())
            .build();
   }
}
