package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.ReactionDto;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.mapper.NotificationMapper;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Notification;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.Reaction;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.NotificationRepository;
import me.min.xulgon.repository.ReactionRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ReactionService {
   private final ReactionRepository reactionRepository;
   private final NotificationMapper notificationMapper;
   private final NotificationRepository notificationRepository;
   private final ContentService contentService;
   private final ContentRepository contentRepository;
   private final AuthenticationService authService;
   private final SimpMessagingTemplate simpMessagingTemplate;

   @Transactional
   public void react(ReactionDto reactionDto) {
      Content content = contentRepository.findById(reactionDto.getContentId())
            .orElseThrow(ContentNotFoundException::new);

      Optional<Reaction> reactionOptional = reactionRepository.findTopByContentAndUserOrderByIdDesc(
            content, authService.getPrincipal()
      );

      if (reactionOptional.isPresent()) {
         if (!reactionOptional.get().getType().equals(reactionDto.getType())) {
            reactionRepository.save(mapToReaction(reactionDto, content));
         }
         reactionRepository.deleteById(reactionOptional.get().getId());
         modifyContentReactionCount(-1, content);
         return;
      }
      reactionRepository.save(mapToReaction(reactionDto, content));
      modifyContentReactionCount(1, content);

//      if (!content.getUser().equals(authService.getPrincipal())) {
//         Notification notif = Notification.builder()
//               .actor(authService.getPrincipal())
//               .recipient(content.getUser())
//               .isRead(false)
//               .type(NotificationType.REACTION)
//               .content(content)
//               .createdAt(Instant.now())
//               .page(content.getPage())
//               .build();

//         notif = notificationRepository.save(notif);
//         simpMessagingTemplate.convertAndSendToUser(
//               content.getUser().getUsername(),
//               "/queue/notification",
//               notificationMapper.toDto(notif));
//      }
   }

   private void modifyContentReactionCount(Integer amount, Content content) {
      content.setReactionCount(content.getReactionCount() + amount);
      contentService.save(content);
   }

   private Reaction mapToReaction(ReactionDto reactionDto, Content content) {
      return Reaction.builder()
            .type(reactionDto.getType())
            .content(content)
            .user(authService.getPrincipal())
            .build();
   }
}
