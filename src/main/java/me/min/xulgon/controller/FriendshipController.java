package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.service.FriendshipService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friendships")
@AllArgsConstructor
public class FriendshipController {

   private final FriendshipService friendshipService;

   @PostMapping("/{requestId}")
   private ResponseEntity<Void> create(@PathVariable Long requestId) {
      friendshipService.save(requestId);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }
}
