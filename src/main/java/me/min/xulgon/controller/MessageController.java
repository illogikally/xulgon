package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.ConversationNotificationDto;
import me.min.xulgon.dto.MessageRequest;
import me.min.xulgon.dto.MessageResponse;
import me.min.xulgon.mapper.MessageMapper;
import me.min.xulgon.model.Message;
import me.min.xulgon.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages/")
@AllArgsConstructor
public class MessageController {

   private final MessageService messageService;
   private final SimpMessagingTemplate simpMessagingTemplate;
   private final MessageMapper messageMapper;

   @MessageMapping("/chat")
   @SendToUser("/queue/chat")
   public MessageResponse send(@Payload MessageRequest request, Principal principal) {
      Message message = messageService.save(request, principal);
      String receiver = message.getReceiver().getUsername();
      MessageResponse messageResponse = messageMapper.toDto(message);
      simpMessagingTemplate.convertAndSendToUser(receiver,
            "/queue/chat",
            messageResponse);
      return messageResponse;
   }

   @GetMapping("/with/{userId}")
   public ResponseEntity<List<MessageResponse>> getMessagesWith(@PathVariable Long userId) {
      return ResponseEntity.ok(messageService.getMessagesWith(userId));
   }

   @GetMapping("/unread")
   public ResponseEntity<Integer> getUnreadCount() {
      return ResponseEntity.ok(messageService.getUnreadCount());
   }

   @GetMapping("/latest")
   public ResponseEntity<List<ConversationNotificationDto>> getLatest() {
      return ResponseEntity.ok(messageService.getLatest());
   }

   @PutMapping("/{id}/read")
   public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
      messageService.markAsRead(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

}
