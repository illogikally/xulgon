package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.ReactionDto;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Reaction;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class ReactionService {
   private final ReactionRepository reactionRepository;
   private final ContentRepository contentRepository;
   private final AuthenticationService authenticationService;

   @Transactional
   public void react(ReactionDto reactionDto) {
      Content content = contentRepository.findById(reactionDto.getContentId())
            .orElseThrow(() -> new RuntimeException("Content not found"));

      Optional<Reaction> reactionOptional = reactionRepository.findTopByContentAndUserOrderByIdDesc(
            content, authenticationService.getLoggedInUser()
      );

      if (reactionOptional.isPresent()) {
         if (!reactionOptional.get().getType().equals(reactionDto.getType())) {
            reactionRepository.save(mapToReaction(reactionDto, content));
         }
         reactionRepository.deleteById(reactionOptional.get().getId());
         return;
      }
      reactionRepository.save(mapToReaction(reactionDto, content));

   }

   private Reaction mapToReaction(ReactionDto reactionDto, Content content) {
      return Reaction.builder()
            .type(reactionDto.getType())
            .content(content)
            .user(authenticationService.getLoggedInUser())
            .build();
   }
}
