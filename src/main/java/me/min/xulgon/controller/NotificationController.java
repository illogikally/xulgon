package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentNotificationDto;
import me.min.xulgon.dto.NotificationDto;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.model.Notification;
import me.min.xulgon.service.NotificationService;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

   @GetMapping
   public ResponseEntity<OffsetResponse<NotificationDto>> get(OffsetRequest request) {
      return ResponseEntity.ok(notificationService.get(request));
   }

   @PutMapping("/{id}/read")
   public ResponseEntity<Void> read(@PathVariable Long id) {
      notificationService.read(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @GetMapping("/unread")
   public ResponseEntity<Integer> getUnreadCount() {
      return ResponseEntity.ok(notificationService.getUnreadCount());
   }
}
