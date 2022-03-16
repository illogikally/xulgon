package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.PageService;
import me.min.xulgon.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/")
@AllArgsConstructor
public class ProfileController {


   private final BlockService blockService;
   private final ProfileService profileService;
   private PageService pageService;

   @GetMapping("/{id}/friends")
   public ResponseEntity<List<UserDto>> getFriends(@PathVariable Long id) {
      return ResponseEntity.ok(profileService.getFriends(id));
   }

   @GetMapping("/{id}/profile")
   public ResponseEntity<PageHeaderDto> getProfileHeader(@PathVariable Long id) {
      return ResponseEntity.ok(profileService.getPageHeader(id));
   }

   @GetMapping("/{id}/is-blocked")
   public ResponseEntity<Boolean> isBlocked(@PathVariable Long id) {
      return ResponseEntity.ok(blockService.isBlocked(id));
   }

   @GetMapping("/{id}")
   public ResponseEntity<UserPageResponse> getUserProfile(@PathVariable Long id) {
      return ResponseEntity.ok(profileService.getUserProfile(id));
   }

   @PutMapping("/update-avatar")
   public ResponseEntity<Void> updateAvatar(@RequestBody Long photoId) {
      profileService.changeAvatar(photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/upload-avatar")
   public ResponseEntity<PhotoResponse> uploadAvatar(@RequestPart("photoRequest") PhotoRequest request,
                                                     @PathVariable Long id,
                                                     @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(profileService.uploadAvatar(id, request, photo));
   }

   @PutMapping("/update-cover")
   public ResponseEntity<Void> updateCover(@RequestBody Long photoId) {
      profileService.changeCoverPhoto(photoId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/upload-cover")
   public ResponseEntity<PhotoResponse> uploadCover(@RequestPart("photoRequest") PhotoRequest request,
                                                    @PathVariable Long id,
                                                    @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.ok(pageService.uploadCoverPhoto(id, request, photo));
   }

   @GetMapping("{id}/info")
   public ResponseEntity<UserInfoDto> getInfo(@PathVariable Long id) {
      return ResponseEntity.ok(profileService.getUserInfo(id));
   }

   @PutMapping("{id}/info")
   public ResponseEntity<UserInfoDto> getInfo(@PathVariable Long id,
                                              @RequestBody UserInfoDto dto) {
      return ResponseEntity.ok(profileService.saveUserInfo(id, dto));
   }
}
