package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.FriendRequest;
import me.min.xulgon.model.Friendship;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FriendRequestRepository;
import me.min.xulgon.repository.FriendshipRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendshipService {

   private final FriendshipRepository friendshipRepository;
   private final FriendRequestRepository friendRequestRepository;
   private final AuthenticationService authenticationService;

   public void save(Long requestId) {
      FriendRequest request = friendRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Friendresiq not found"));
      friendshipRepository.save(Friendship.builder()
            .createdAt(Instant.now())
            .userA(request.getRequestee())
            .userB(request.getRequestor())
            .build()
      );
      friendRequestRepository.deleteById(requestId);
   }

   public Integer getCommonFriendCount(User user) {
      User loggedInUser = authenticationService.getLoggedInUser();

      List<User> yourFriends = getFriends(loggedInUser);
      List<User> theirFriends = getFriends(user);

      yourFriends.retainAll(theirFriends);
      return yourFriends.size();
   }

   public  List<User> getFriends(User user) {
      User loggedInUser = authenticationService.getLoggedInUser();
      return friendshipRepository.findAllByUser(user).stream()
            .map(friendship -> {
               if (friendship.getUserA().equals(user)) {
                  return friendship.getUserB();
               }
               return friendship.getUserA();
            })
            .filter(u -> !u.getId().equals(loggedInUser.getId()))
            .collect(Collectors.toList());
   }

}
