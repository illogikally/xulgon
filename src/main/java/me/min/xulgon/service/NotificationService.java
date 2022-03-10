package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.NotificationRepository;
import me.min.xulgon.repository.NotificationSubjectRepository;
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

   public List<NotificationDto> get() {
      return notificationSubjectRepository
            .findAllByRecipientOrderByLatestCreatedAtDesc(authenticationService.getPrincipal())
            .stream()
            .filter(subject -> !blockService.isBlockingEachOther(subject.getRecipient(), subject.getLatestNotification().getActor()))
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());
   }

   public void read(Long id) {
      NotificationSubject notification = notificationSubjectRepository.findById(id)
            .orElseThrow(RuntimeException::new);

      notification.setIsRead(true);
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
      simpMessagingTemplate.convertAndSendToUser(
            subject.getRecipient().getUsername(),
            "/queue/notification",
            notificationMapper.toDto(subject));
   }
}
