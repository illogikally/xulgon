package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.min.xulgon.dto.MessageRequest;
import me.min.xulgon.dto.MessageResponse;
import me.min.xulgon.model.Message;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;

@Service
@Transactional
@AllArgsConstructor
public class MessageMapper {

   private final UserRepository userRepository;

   @Transactional
   public MessageResponse toDto(Message message) {
      return MessageResponse.builder()
            .username(getUsername(message))
            .id(message.getId())
            .userAvatarUrl(message.getSender().getProfile().getAvatar().getUrl())
            .createdAgo(MappingUtil.getCreatedAgo(message.getCreatedAt()))
            .userId(message.getSender().getId())
            .message(message.getMessage())
            .build();

   }

   public Message map(MessageRequest request, Principal principal) {
      return Message.builder()
            .sender(getSender(principal))
            .receiver(getReceiver(request))
            .createdAt(Instant.now())
            .seen(false)
            .message(request.getMessage())
            .build();
   }

   private User getSender(Principal principal) {
      return userRepository.findByUsername(principal.getName())
            .orElseThrow(RuntimeException::new);
   }

   String getUsername(Message message) {
      return message.getSender().getLastName() + " " + message.getSender().getLastName();
   }

   User getReceiver(MessageRequest request) {
      return userRepository.findById(request.getReceiverId())
            .orElseThrow(RuntimeException::new);
   }
}
