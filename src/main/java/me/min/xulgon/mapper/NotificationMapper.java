package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationContentDto;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.dto.UserBasicDto;
import me.min.xulgon.model.*;
import me.min.xulgon.util.Util;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@AllArgsConstructor
public class NotificationMapper {

   private final Environment env;

   public NotificationDto toDto(NotificationSubject notification, boolean isPreviousRead) {
      Notification latest = notification.getLatestNotification();
      Page page = notification.getPage();
      Content subjectContent = notification.getSubjectContent();
      Content rootContent = notification.getRootContent();
      return NotificationDto
            .builder()
            .id(notification.getId())
            .isRead(notification.getIsRead())
            .isPreviousRead(isPreviousRead)
            .type(notification.getType())
            .actorCount(notification.getActorCount())
            .createdAgo(MappingUtil.getCreatedAgo(notification.getLatestCreatedAt()))
            .pageId(page.getId())
            .pageType(page.getType())
            .pageName(page.getName())

            .actor(toBasicDto(latest.getActor()))
            .actorContent(toNotificationContentDto(getNull(Notification::getActorContent, latest)))
            .rootContent(toNotificationContentDto(rootContent))
            .targetContent(toNotificationContentDto(subjectContent))
            .targetContentParent(toNotificationContentDto(getNull(Content::getParentContent, subjectContent)))
            .build();
   }

   private UserBasicDto toBasicDto(User user) {
      return UserBasicDto.builder()
            .avatarUrl(Util.getThumbnailUrl(env, user.getProfile().getAvatar().getThumbnailsMap().get(ThumbnailType.s40x40)))
            .username(user.getFullName())
            .profileId(user.getProfile().getId())
            .id(user.getId())
            .build();
   }

   private NotificationContentDto toNotificationContentDto(Content content) {
      if (content == null) return null;
      return NotificationContentDto.builder()
            .id(content.getId())
            .text(content.getBody())
            .type(content.getType())
            .user(toBasicDto(content.getUser()))
            .build();
   }
   private <T, R> R getNull(Function<T , R> get, T object) {
      return object == null ? null : get.apply(object);
   }
}
