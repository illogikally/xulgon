package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserProfileResponse;
import me.min.xulgon.model.UserProfile;
import me.min.xulgon.service.PostService;
import me.min.xulgon.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/")
@AllArgsConstructor
public class UserProfileController {

   private final PostService postService;
   private final UserProfileService userProfileService;

   @GetMapping("/{id}/posts")
   public ResponseEntity<List<PostResponse>> getPostsByPage(@PathVariable Long id) {
       return ResponseEntity.ok(postService.getPostsByProfile(id));
   }

   @GetMapping("/{id}")
   public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
      return ResponseEntity.ok(userProfileService.getUserProfile(id));
   }

}
