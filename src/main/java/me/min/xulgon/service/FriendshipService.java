package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.FriendRequestRepository;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class FriendshipService {


   private final FollowRepository followRepository;
   private final FriendshipRepository friendshipRepository;
   private final FriendRequestRepository friendRequestRepository;
   private final AuthenticationService authenticationService;
   private final BlockService blockService;
   private final UserRepository userRepository;

   public void createFriendship(Long requesterId) {
      User requester = userRepository.findById(requesterId)
            .orElseThrow(() -> new RuntimeException("User not found"));
      FriendRequest request = friendRequestRepository.findByRequesterAndRequestee(
            requester, authenticationService.getPrincipal()
      )
            .orElseThrow(() -> new RuntimeException("Friendresiq not found"));

      User principal = authenticationService.getPrincipal();
      friendshipRepository.save(Friendship.builder()
            .createdAt(Instant.now())
            .userA(request.getRequestee())
            .userB(request.getRequester())
            .build()
      );

      friendRequestRepository.deleteById(request.getId());
      followRepository.findByUserAndPage(principal, requester.getUserPage())
            .orElseGet(() -> followRepository.save(Follow.builder()
                  .page(requester.getUserPage())
                  .createdAt(Instant.now())
                  .user(principal)
                  .build())
            );

      followRepository.findByUserAndPage(requester, principal.getUserPage())
            .orElseGet(() -> followRepository.save(Follow.builder()
                  .page(principal.getUserPage())
                  .createdAt(Instant.now())
                  .user(requester)
                  .build())
            );
   }

   public void delete(Long userId) {
      User principal = authenticationService.getPrincipal();
      User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
      friendshipRepository.deleteByUsers(user, principal);
      followRepository.deleteByUserAndPage(user, principal.getUserPage());
      followRepository.deleteByUserAndPage(principal, user.getUserPage());
   }

   public Integer getCommonFriendCount(User user) {
      User principal = authenticationService.getPrincipal();

      List<User> yourFriends = getFriends(principal);
      List<User> theirFriends = getFriends(user);

      yourFriends.retainAll(theirFriends);
      return yourFriends.size();
   }

   public List<User> getFriends(User user) {
      return friendshipRepository.findAllByUser(user)
            .stream()
            .map(friendship -> {
               if (friendship.getUserA().equals(user)) {
                  return friendship.getUserB();
               }
               return friendship.getUserA();
            })
            .filter(blockService::filter)
            .collect(Collectors.toList());
   }

   public FriendshipStatus getFriendshipStatus(User user ) {
      FriendshipStatus status = null;
      User principal = authenticationService.getPrincipal();

      if (friendshipRepository.findByUsers(user, principal).isPresent()) {
         status = FriendshipStatus.FRIEND;
      }

      else if (friendRequestRepository.
            findByRequesterAndRequestee(principal, user).isPresent()) {
         status = FriendshipStatus.SENT;
      }
      else if (friendRequestRepository.
            findByRequesterAndRequestee(user, principal).isPresent()) {
         status = FriendshipStatus.RECEIVED;
      }
      else if (principal != user) {
         status = FriendshipStatus.NULL;
      }

      return status;
   }

}
