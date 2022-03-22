package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.ReactionDto;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ReactionService {
   private final ReactionRepository reactionRepository;
   private final ContentService contentService;
   private final ContentRepository contentRepository;
   private final AuthenticationService authService;
   private NotificationService notificationService;

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

      Reaction reaction = reactionRepository.save(mapToReaction(reactionDto, content));
      modifyContentReactionCount(1, content);
      createReactNotification(reaction);
   }

   private void createReactNotification(Reaction reaction) {
      Content content = reaction.getContent();
      Content rootContent = content.getType().equals(ContentType.COMMENT)
            ? ((Comment) content).getRootContent()
            : content;

      notificationService.createNotification(
            reaction.getUser(),
            content,
            rootContent,
            content,
            content.getPage(),
            content.getUser(),
            NotificationType.REACTION
      );
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
