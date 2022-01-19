package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.Block;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.User;
import me.min.xulgon.model.UserPage;
import me.min.xulgon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class BlockService {

   private final BlockRepository blockRepository;
   private final AuthenticationService authService;
   private final UserPageRepository userPageRepository;
   private final FriendRequestRepository friendRequestRepository;
   private final UserRepository userRepository;
   private final FriendshipRepository friendshipRepository;

   public Boolean blocked(User user) {
      return blockRepository.findByBlockerAndBlockee(
            authService.getPrincipal(), user
      ).isPresent();
   }

   @Transactional(readOnly = true)
   public List<Long> getBlockersId() {
      return blockRepository.findAllByBlockee(authService.getPrincipal())
            .stream()
            .map(Block::getBlocker)
            .map(User::getId)
            .collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   public boolean filter(User user) {
      return getBlockersId().stream()
            .noneMatch(blockerId -> blockerId.equals(user.getId()));
   }

   @Transactional(readOnly = true)
   public boolean filter(Content content) {
      return this.filter(content.getUser());
   }

   @Transactional(readOnly = true)
   public Boolean isBlocked(Long profileId) {
      UserPage profile = userPageRepository.findById(profileId)
            .orElseThrow(RuntimeException::new);

      return blockRepository.findByBlockerAndBlockee(profile.getUser(), authService.getPrincipal())
            .isPresent();
   }

   public void block(Long blockeeId) {
      User blockee = userRepository.findById(blockeeId)
            .orElseThrow(RuntimeException::new);
      Block block = Block.builder()
            .blocker(authService.getPrincipal())
            .blockee(blockee)
            .createdAt(Instant.now())
            .build();

      friendshipRepository.deleteByUsers(authService.getPrincipal(), blockee);
      friendRequestRepository.deleteByUsers(authService.getPrincipal(), blockee);
      blockRepository.save(block);
   }

   public void unblock(Long blockeeId) {
      User blockee = userRepository.findById(blockeeId)
            .orElseThrow(RuntimeException::new);

      blockRepository.deleteByBlockerAndBlockee(authService.getPrincipal(), blockee);

   }
}
