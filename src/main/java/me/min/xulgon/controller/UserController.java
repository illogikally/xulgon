package me.min.xulgon.controller;

import io.swagger.models.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.FriendRequestDto;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FriendRequestRepository;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.service.FriendRequestService;
import me.min.xulgon.service.FriendshipService;
import me.min.xulgon.service.TimelineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

   private final FriendRequestService friendRequestService;
   private final FriendshipRepository friendshipRepository;
   private final UserRepository userRepository;
   private final FriendshipService friendshipService;
   private final TimelineService timelineService;

   @PostMapping("/{id}/friend-requests")
   public void save(@PathVariable Long id) {
      friendRequestService.save(id);
   }

   @DeleteMapping("/{id}/friend-requests")
   public void delete(@PathVariable Long id) {
      friendRequestService.delete(id);
   }

   @GetMapping("/{id}/friend-requests")
   public ResponseEntity<List<FriendRequestDto>> get(@PathVariable Long id) {
      return ResponseEntity.ok(friendRequestService.getRequestsByRequestee(id));
   }

   @GetMapping("/{id}/friends")
   public void getFriends(@PathVariable Long id) {
      User user = userRepository.findById(id).get();
      friendshipRepository.findAllByUser(user).forEach(friendship -> {
         log.error(friendship.getUserA() + " " + friendship.getUserB());
      });
   }

   @GetMapping("/timeline")
   public ResponseEntity<List<PostResponse>> getTimeline() {
      return ResponseEntity.ok(timelineService.getTimeline());
   }
}
