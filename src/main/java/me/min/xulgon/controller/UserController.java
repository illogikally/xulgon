package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.*;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.service.*;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

   private final FriendRequestService friendRequestService;
   private final BlockService blockService;
   private final FriendshipService friendshipService;
   private final UserService userService;

   @PostMapping("/{id}/friend-requests")
   public void save(@PathVariable Long id) {
      friendRequestService.save(id);
   }

   @DeleteMapping("/{id}/friend-requests")
   public void deleteFriendRequest(@PathVariable Long id) {
      friendRequestService.delete(id);
   }

   @GetMapping("/{id}/friend-requests")
   public ResponseEntity<List<FriendRequestDto>> get(@PathVariable Long id) {
      return ResponseEntity.ok(friendRequestService.getRequestsByRequestee(id));
   }

   @GetMapping("/{id}/friends")
   public ResponseEntity<List<UserDto>> getFriends(@PathVariable Long id) {
      return ResponseEntity.ok(userService.getFriends(id));
   }

   @DeleteMapping("/{id}/friends")
   public ResponseEntity<Void> deleteFriend(@PathVariable Long id) {
      friendshipService.delete(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PostMapping("/{id}/friends")
   public ResponseEntity<Void> createFriend(@PathVariable Long id) {
      friendshipService.createFriendship(id);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }

   @GetMapping("/news-feed")
   public ResponseEntity<List<PostResponse>> getNewsFeed(OffsetRequest pageable) {
      System.out.println(pageable.toString());
      return ResponseEntity.ok(userService.getNewsFeed(pageable));
   }

   @GetMapping("/groups")
   public ResponseEntity<List<GroupResponse>> getJoinedGroups() {
      return ResponseEntity.ok(userService.getJoinedGroups());
   }

   @PostMapping("/{id}/block")
   public ResponseEntity<Void> block(@PathVariable Long id) {
      blockService.block(id);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }

   @DeleteMapping("/{id}/block")
   public ResponseEntity<Void> unblock(@PathVariable Long id) {
      blockService.unblock(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @GetMapping("/group-feed")
   public ResponseEntity<OffsetResponse<PostResponse>> getGroupFeed(OffsetRequest pageable) {
      return ResponseEntity.ok(userService.getGroupFeed(pageable));
   }

   @GetMapping("/existed")
   public ResponseEntity<Boolean> existed(@RequestParam("username") String username) {
      return ResponseEntity.ok(userService.isUserExisted(username));
   }

   @GetMapping("{id}/basic-friends")
   public ResponseEntity<List<UserBasicDto>> getBasicFriends(@PathVariable Long id) {
      return ResponseEntity.ok(userService.getBasicFriends(id));
   }
}
