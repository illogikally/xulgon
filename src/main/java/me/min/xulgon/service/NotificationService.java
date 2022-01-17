package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.NotifCountDto;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.Notification;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService {

   private final NotificationMapper notificationMapper;
   private final AuthenticationService authService;
   private final NotificationRepository notifRepository;

   public NotifCountDto getNotifCount() {
      User user = authService.getLoggedInUser();
      return NotifCountDto.builder()
            .unreadMessageCount(user.getUnreadMessageCount())
            .unreadNotifCount(user.getUnreadNotificationCount())
            .build();
   }

   public List<NotificationDto> get() {
      return List.of(NotificationDto.builder().build());
//      return notifRepository
//            .findAllByRecipientOrderByCreatedAtDesc(authService.getLoggedInUser())
//            .stream()
//            .map(notificationMapper::toDto)
//            .collect(Collectors.toList());
   }

   public void read(Long id) {
      Notification notif = notifRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      notif.setIsRead(true);
      notifRepository.save(notif);
   }
}
