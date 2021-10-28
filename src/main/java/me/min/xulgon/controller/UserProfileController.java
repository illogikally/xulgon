package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.PhotoService;
import me.min.xulgon.service.PostService;
import me.min.xulgon.service.UserProfileService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/")
@AllArgsConstructor
public class UserProfileController {

   private final PostService postService;

   private final BlockService blockService;
   private final UserProfileService userProfileService;
   private final PhotoService photoService;

   @GetMapping("/{id}/posts")
   public ResponseEntity<List<PostResponse>> getPostsByPage(@PathVariable Long id, Pageable pageable) {
       return ResponseEntity.ok(postService.getPostsByProfile(id, pageable));
   }

   @GetMapping("/{id}/friends")
   public ResponseEntity<List<UserDto>> getFriends(@PathVariable Long id) {
      return ResponseEntity.ok(userProfileService.getFriends(id));
   }

   @GetMapping("/{id}/is-blocked")
   public ResponseEntity<Boolean> isBlocked(@PathVariable Long id) {
      return ResponseEntity.ok(blockService.isBlocked(id));
   }

   @GetMapping("/{id}")
   public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
      return ResponseEntity.ok(userProfileService.getUserProfile(id));
   }

   @PutMapping("/{id}/update-avatar")
   public ResponseEntity<Void> updateAvatar(@PathVariable Long id,
                                            @RequestBody Long photoId) {
      userProfileService.updateAvatar(id, photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/upload-avatar")
   public ResponseEntity<PhotoViewResponse> uploadAvatar(@PathVariable Long id,
                                                         @RequestPart("photoRequest") PhotoRequest request,
                                                         @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(userProfileService.uploadAvatar(id, request, photo));
   }

   @PutMapping("/{id}/update-cover")
   public ResponseEntity<Void> updateCover(@PathVariable Long id,
                                            @RequestBody Long photoId) {
      userProfileService.updateCover(id, photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/upload-cover")
   public ResponseEntity<PhotoViewResponse> uploadCover(@PathVariable Long id,
                                                        @RequestPart("photoRequest") PhotoRequest request,
                                                        @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(userProfileService.uploadCover(id, request, photo));
   }


}
