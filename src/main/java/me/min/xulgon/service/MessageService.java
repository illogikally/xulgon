package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.MessageRequest;
import me.min.xulgon.dto.MessageResponse;
import me.min.xulgon.mapper.MessageMapper;
import me.min.xulgon.model.Message;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.MessageRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageService {

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
      return userRepository.findByUsername(((UsernamePasswordAuthenticationToken) principal).getName())
            .orElseThrow(RuntimeException::new);
   }

   public MessageResponse toDto(Message message) {
      return messageMapper.toDto(message);
   }
}
