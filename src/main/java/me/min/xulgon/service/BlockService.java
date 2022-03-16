package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.model.Block;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.User;
import me.min.xulgon.model.Profile;
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
   private final ProfileRepository profileRepository;
   private final FollowRepository followRepository;
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
      Profile profile = profileRepository.findById(profileId)
            .orElseThrow(PageNotFoundException::new);

      return blockRepository.findByBlockerAndBlockee(profile.getUser(), authService.getPrincipal())
            .isPresent();
   }

   public void block(Long blockeeId) {
      User blockee = userRepository.findById(blockeeId)
            .orElseThrow(UserNotFoundException::new);
      Block block = Block.builder()
            .blocker(authService.getPrincipal())
            .blockee(blockee)
            .createdAt(Instant.now())
            .build();

      friendshipRepository.deleteByUsers(authService.getPrincipal(), blockee);
      friendRequestRepository.deleteByUsers(authService.getPrincipal(), blockee);
      followRepository.deleteByFollowerAndPage(authService.getPrincipal(), blockee.getProfile());
      followRepository.deleteByFollowerAndPage(blockee, authService.getPrincipal().getProfile());
      blockRepository.save(block);
   }

   public void unblock(Long blockeeId) {
      User blockee = userRepository.findById(blockeeId)
            .orElseThrow(UserNotFoundException::new);

      blockRepository.deleteByBlockerAndBlockee(authService.getPrincipal(), blockee);

   }

   public Boolean isBlockingEachOther(User userA, User userB) {
      boolean isABlockingB = blockRepository.findByBlockerAndBlockee(userA, userB)
            .isPresent();
      if (isABlockingB) return true;
      return blockRepository.findByBlockerAndBlockee(userB, userA).isPresent();
   }

}
