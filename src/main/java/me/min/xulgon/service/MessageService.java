package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.ConversationNotifDto;
import me.min.xulgon.dto.MessageRequest;
import me.min.xulgon.dto.MessageResponse;
import me.min.xulgon.dto.UserBasicDto;
import me.min.xulgon.mapper.MessageMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.Message;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.MessageRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class MessageService {

   private final UserMapper userMapper;
   private final MessageRepository messageRepository;
   private final AuthenticationService authService;
   private final UserRepository userRepository;
   private final MessageMapper messageMapper;



   public Message save(MessageRequest request, Principal principal) {
      return messageRepository.save(messageMapper.map(request, principal));
   }

   public List<MessageResponse> getMessagesWith(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(RuntimeException::new);

      return messageRepository.findAllByUsers(user, authService.getLoggedInUser())
            .stream()
            .map(messageMapper::toDto)
            .collect(Collectors.toList());

   }

   public User getLoggedInUser(Principal principal) {
      return userRepository.findByUsername(principal.getName())
            .orElseThrow(RuntimeException::new);
   }

   public MessageResponse toDto(Message message) {
      return messageMapper.toDto(message);
   }

   public Integer getUnreadCount() {
      return messageRepository.countUnread(this.authService.getLoggedInUser().getId());
   }

   public List<ConversationNotifDto> getLatest() {
      return messageRepository.getRecentConversations(authService.getLoggedInUser().getId())
            .stream()
            .map(this::conversationNotifMapper)
            .collect(Collectors.toList());
   }

   public void markAsRead(Long id) {
      Message message = messageRepository.findById(id)
            .orElseThrow(RuntimeException::new);

      message.setIsRead(true);
      messageRepository.save(message);
   }

   private ConversationNotifDto conversationNotifMapper(Message message) {
      User participant = message.getSender().equals(authService.getLoggedInUser())
            ? message.getReceiver() : message.getSender();

      return ConversationNotifDto.builder()
            .latestMessage(messageMapper.toDto(message))
            .id(message.getConversation().getId())
            .user(UserBasicDto.builder()
                  .avatarUrl(participant.getProfile().getAvatar().getUrl())
                  .username(participant.getFullName())
                  .id(participant.getId())
                  .build())
            .build();
   }

}

