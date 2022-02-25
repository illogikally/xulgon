package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.model.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationMapper {

   private final PhotoMapper photoMapper;

   public NotificationDto toDto(NotificationSubject notification) {
      Notification latest = notification.getLatestNotification();
      Page page = notification.getPage();
      Photo avatar = latest.getActor().getUserPage().getAvatar();
      return NotificationDto
            .builder()
            .id(notification.getId())
            .isRead(notification.getIsRead())
            .type(notification.getType())
            .createdAgo(MappingUtil.getCreatedAgo(notification.getLatestCreatedAt()))

            .actorAvatarUrl(photoMapper.getUrl(avatar))
            .actorFullName(latest.getActor().getFullName())
            .actorContentId(latest.getActorContent().getId())
            .actorCount(notification.getActorCount())
            .actorId(latest.getActor().getId())

            .pageId(page.getId())
            .pageType(page.getType())

            .rootContentId(notification.getRootContent().getId())
            .rootContentType(notification.getRootContent().getType())

            .recipientContentBody(notification.getSubjectContent().getBody())
            .recipientContentId(notification.getSubjectContent().getId())
            .recipientContentBody(notification.getSubjectContent().getBody())
            .recipientContentType(notification.getSubjectContent().getType())
            .build();
   }
}
