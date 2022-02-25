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
public class UserPageController {


   private final BlockService blockService;
   private final UserPageService userPageService;

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

   @PutMapping("/update-avatar")
   public ResponseEntity<Void> updateAvatar(@RequestBody Long photoId) {
      userPageService.changeAvatar(photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/upload-avatar")
   public ResponseEntity<PhotoResponse> uploadAvatar(@RequestPart("photoRequest") PhotoRequest request,
                                                         @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(userPageService.uploadAvatar(request, photo));
   }

   @PutMapping("/update-cover")
   public ResponseEntity<Void> updateCover(@RequestBody Long photoId) {
      userPageService.changeCoverPhoto(photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/upload-cover")
   public ResponseEntity<PhotoResponse> uploadCover(@RequestPart("photoRequest") PhotoRequest request,
                                                        @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(userPageService.uploadCoverPhoto(request, photo));
   }


}
