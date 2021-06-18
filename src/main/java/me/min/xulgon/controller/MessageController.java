package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.MessageRequest;
import me.min.xulgon.dto.MessageResponse;
import me.min.xulgon.mapper.MessageMapper;
import me.min.xulgon.model.Message;
import me.min.xulgon.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
   public void send(@Payload MessageRequest request, Principal principal) {
      Message message = messageService.save(request, principal);
      String receiver = message.getReceiver().getUsername();
      simpMessagingTemplate.convertAndSendToUser(receiver,
            "/queue/chat",
            messageMapper.toDto(message));

   }

   @GetMapping("/with/{userId}")
   public ResponseEntity<List<MessageResponse>> getMessagesWith(@PathVariable Long userId) {
      return ResponseEntity.ok(messageService.getMessagesWith(userId));
   }



}
