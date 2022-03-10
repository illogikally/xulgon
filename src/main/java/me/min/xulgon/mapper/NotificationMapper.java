package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.model.*;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@AllArgsConstructor
public class NotificationMapper {

   private final UserMapper userMapper;
   private final ContentMapper contentMapper;

   public NotificationDto toDto(NotificationSubject notification) {
      Notification latest = notification.getLatestNotification();
      Page page = notification.getPage();
      Content subjectContent = notification.getSubjectContent();
      Content rootContent = notification.getRootContent();
      return NotificationDto
            .builder()
            .id(notification.getId())
            .isRead(notification.getIsRead())
            .type(notification.getType())
            .actorCount(notification.getActorCount())
            .createdAgo(MappingUtil.getCreatedAgo(notification.getLatestCreatedAt()))
            .pageId(page.getId())
            .pageType(page.getType())
            .pageName(page.getName())

            .actor(userMapper.toBasicDto(latest.getActor()))
            .actorContent(contentMapper.toNotificationContentDto(getNull(Notification::getActorContent, latest)))
            .rootContent(contentMapper.toNotificationContentDto(rootContent))
            .targetContent(contentMapper.toNotificationContentDto(subjectContent))
            .targetContentParent(contentMapper.toNotificationContentDto(getNull(Content::getParentContent, subjectContent)))
            .build();
   }

   private <T, R> R getNull(Function<T , R> get, T object) {
      return object == null ? null : get.apply(object);
   }
}
