package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.PhotoService;
import me.min.xulgon.service.PostService;
import me.min.xulgon.service.UserPageService;
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
   private final UserPageService userPageService;
   private final PhotoService photoService;

//   @GetMapping("/{id}/posts")
//   public ResponseEntity<List<PostResponse>> getPostsByPage(@PathVariable Long id, LimPageable pageable) {
//       return ResponseEntity.ok(postService.getPostsByProfile(id, pageable));
//   }

   @GetMapping("/{id}/friends")
   public ResponseEntity<List<UserDto>> getFriends(@PathVariable Long id) {
      return ResponseEntity.ok(userPageService.getFriends(id));
   }

   @GetMapping("/{id}/profile")
   public ResponseEntity<PageHeaderDto> getProfileHeader(@PathVariable Long id) {
      return ResponseEntity.ok(userPageService.getPageHeader(id));
   }

   @GetMapping("/{id}/is-blocked")
   public ResponseEntity<Boolean> isBlocked(@PathVariable Long id) {
      return ResponseEntity.ok(blockService.isBlocked(id));
   }

   @GetMapping("/{id}")
   public ResponseEntity<UserPageResponse> getUserProfile(@PathVariable Long id) {
      return ResponseEntity.ok(userPageService.getUserProfile(id));
   }

   @PutMapping("/{id}/update-avatar")
   public ResponseEntity<Void> updateAvatar(@PathVariable Long id,
                                            @RequestBody Long photoId) {
      userPageService.changeAvatar(id, photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/upload-avatar")
   public ResponseEntity<PhotoViewResponse> uploadAvatar(@PathVariable Long id,
                                                         @RequestPart("photoRequest") PhotoRequest request,
                                                         @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(userPageService.uploadAvatar(id, request, photo));
   }

   @PutMapping("/{id}/update-cover")
   public ResponseEntity<Void> updateCover(@PathVariable Long id,
                                            @RequestBody Long photoId) {
      userPageService.changeCoverPhoto(id, photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/upload-cover")
   public ResponseEntity<PhotoResponse> uploadCover(@PathVariable Long id,
                                                        @RequestPart("photoRequest") PhotoRequest request,
                                                        @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(userPageService.uploadCoverPhoto(id, request, photo));
   }


}
