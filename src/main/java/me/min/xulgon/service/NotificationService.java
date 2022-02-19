package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotificationCountDto;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.NotificationSubject;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.NotificationSubjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

   private final NotificationMapper notificationMapper;
   private final AuthenticationService authenticationService;
   private final NotificationSubjectRepository notificationSubjectRepository;

   public NotificationCountDto getNotificationCount() {
      User user = authenticationService.getPrincipal();
      return NotificationCountDto.builder()
            .unreadMessageCount(user.getUnreadMessageCount())
            .unreadNotifCount(user.getUnreadNotificationCount())
            .build();
   }

   public List<NotificationDto> get() {
      return notificationSubjectRepository
            .findAllByRecipientOrderByLatestCreatedAtDesc(authenticationService.getPrincipal())
            .stream()
            .map(notificationMapper::toDto)
            .collect(Collectors.toList());
   }

   public void read(Long id) {
      NotificationSubject notification = notificationSubjectRepository.findById(id)
            .orElseThrow(RuntimeException::new);

      notification.setIsRead(true);
      notificationSubjectRepository.save(notification);
   }
}
