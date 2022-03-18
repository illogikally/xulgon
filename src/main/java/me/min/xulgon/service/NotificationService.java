package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.BlockRepository;
import me.min.xulgon.repository.NotificationRepository;
import me.min.xulgon.repository.NotificationSubjectRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

   private final NotificationMapper notificationMapper;
   private final AuthenticationService authenticationService;
   private final NotificationSubjectRepository notificationSubjectRepository;
   private NotificationRepository notificationRepository;
   private SimpMessagingTemplate simpMessagingTemplate;
   private BlockService blockService;
   private final UserRepository userRepository;

   public OffsetResponse<NotificationDto> get(OffsetRequest request) {
      var notifications = notificationSubjectRepository
            .findAllByRecipientOrderByIsReadAscLatestCreatedAtDesc(
                  authenticationService.getPrincipal(),
                  request.sizePlusOne()
      );
      boolean hasNext = notifications.size() > request.getPageSize();
      var responses = notifications
            .stream()
//            .filter(subject -> !blockService.isBlockingEachOther(subject.getRecipient(), subject.getLatestNotification().getActor()))
            .limit(request.getPageSize())
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());

      return OffsetResponse
            .<NotificationDto>builder()
            .data(responses)
            .hasNext(hasNext)
            .build();
   }

   public void read(Long id) {
      NotificationSubject notification = notificationSubjectRepository.findById(id)
            .orElseThrow(RuntimeException::new);

      notification.setIsRead(true);
      User principal = authenticationService.getPrincipal();
      principal.setUnreadNotificationCount(principal.getUnreadMessageCount() - 1);
      userRepository.save(principal);
      notificationSubjectRepository.save(notification);
   }

   public void createNotification(User actor,
                                  Content targetContent,
                                  Content rootContent,
                                  Content actorContent,
                                  Page page,
                                  User recipient,
                                  NotificationType notificationType) {

      if (recipient.equals(actor) || blockService.isBlockingEachOther(actor, recipient)) return;

      Notification notification = Notification
            .builder()
            .createdAt(Instant.now())
            .actor(actor)
            .actorContent(actorContent)
            .build();

      NotificationSubject subject =
            notificationSubjectRepository.findByRecipientAndSubjectContentAndType(
                  recipient,
                  targetContent,
                  notificationType
            ).orElseGet(() ->
                  NotificationSubject
                        .builder()
                        .type(notificationType)
                        .isDisabled(false)
                        .actorCount(0)
                        .rootContent(rootContent)
                        .page(page)
                        .subjectContent(targetContent)
                        .notifications(List.of())
                        .recipient(recipient)
                        .build()
            );

      notification = notificationRepository.save(notification);
      subject.setLatestNotification(notification);
      if (subject.getNotifications()
            .stream()
            .noneMatch(n -> n.getActor().equals(actor))
      ) {
         subject.setActorCount(subject.getActorCount() + 1);
      }
      subject.setLatestCreatedAt(notification.getCreatedAt());
      subject = notificationSubjectRepository.save(subject);
      subject.setIsRead(false);
      notification.setSubject(subject);
      notificationRepository.save(notification);
      recipient.setUnreadNotificationCount(recipient.getUnreadNotificationCount() + 1);
      userRepository.save(recipient);
      simpMessagingTemplate.convertAndSendToUser(
            subject.getRecipient().getUsername(),
            "/queue/notification",
            notificationMapper.toDto(subject));
   }

   public Integer getUnreadCount() {
      User principal = authenticationService.getPrincipal();
      return principal.getUnreadNotificationCount();
   }
}
