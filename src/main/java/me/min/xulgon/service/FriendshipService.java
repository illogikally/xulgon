package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.UserNotFoundException;
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
   private final NotificationService notificationService;

   public void createFriendship(Long requesterId) {
      User requester = userRepository.findById(requesterId)
            .orElseThrow(UserNotFoundException::new);

      FriendRequest request = friendRequestRepository.findByRequesterAndRequestee(
            requester, authenticationService.getPrincipal()
      ).orElse(null);

      if (request == null) return;

      User principal = authenticationService.getPrincipal();


      friendshipRepository.save(Friendship.builder()
            .createdAt(Instant.now())
            .userA(request.getRequestee())
            .userB(request.getRequester())
            .build()
      );

      friendRequestRepository.deleteByUsers(requester, principal);
      followRepository.findByFollowerAndPage(principal, requester.getProfile())
            .orElseGet(() -> followRepository.save(Follow.builder()
                  .page(requester.getProfile())
                  .createdAt(Instant.now())
                  .follower(principal)
                  .build())
            );

      followRepository.findByFollowerAndPage(requester, principal.getProfile())
            .orElseGet(() -> followRepository.save(Follow.builder()
                  .page(principal.getProfile())
                  .createdAt(Instant.now())
                  .follower(requester)
                  .build())
            );

      notificationService.createNotification(
            principal,
            null, null, null,
            principal.getProfile(),
            requester,
            NotificationType.FRIEND_REQUEST_ACCEPT
      );
   }

   public void delete(Long userId) {
      User principal = authenticationService.getPrincipal();
      User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
      friendshipRepository.deleteByUsers(user, principal);
      followRepository.deleteByFollowerAndPage(user, principal.getProfile());
      followRepository.deleteByFollowerAndPage(principal, user.getProfile());
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
