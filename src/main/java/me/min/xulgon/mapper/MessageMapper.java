package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.MessageRequest;
import me.min.xulgon.dto.MessageResponse;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.model.Conversation;
import me.min.xulgon.model.Message;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.ConversationRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;

@Service
@Transactional
@AllArgsConstructor
public class MessageMapper {

   private final ConversationRepository conversationRepository;
   private final UserRepository userRepository;
   private final PhotoMapper photoMapper;

   @Transactional
   public MessageResponse toDto(Message message) {
      Photo senderAvatar = message.getSender().getProfile().getAvatar();
      return MessageResponse.builder()
            .username(getUsername(message))
            .id(message.getId())
            .userAvatarUrl(photoMapper.getUrl(senderAvatar))
            .isRead(message.getIsRead())
            .conversationId(message.getConversation().getId())
            .createdAgo(MappingUtil.getCreatedAgo(message.getCreatedAt()))
            .createdAt(message.getCreatedAt().toEpochMilli())
            .userId(message.getSender().getId())
            .message(message.getMessage())
            .build();
   }

   public Message map(MessageRequest request, Principal principal) {
      return Message.builder()
            .sender(getSender(principal))
            .receiver(getReceiver(request))
            .conversation(getConversation(request))
            .createdAt(Instant.now())
            .isRead(false)
            .message(request.getMessage())
            .build();
   }

   private Conversation getConversation(MessageRequest request) {
      return request.getConversationId() != null
            ? conversationRepository.findById(request.getConversationId()).orElseThrow(RuntimeException::new)
            : conversationRepository.save(new Conversation());
   }


   private User getSender(Principal principal) {
      return userRepository.findByUsername(principal.getName())
            .orElseThrow(UserNotFoundException::new);
   }

   String getUsername(Message message) {
      return message.getSender().getFullName();
   }

   User getReceiver(MessageRequest request) {
      return userRepository.findById(request.getReceiverId())
            .orElseThrow(UserNotFoundException::new);
   }
}
