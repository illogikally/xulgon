package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.service.PhotoSetPhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/photo-sets")
@AllArgsConstructor
public class PhotoSetController {

   private final PhotoSetPhotoService photoSetPhotoService;


   @GetMapping("/{setId}/photos/{photoId}")
   public ResponseEntity<PhotoViewResponse> get(@PathVariable Long setId,
                                                @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetPhotoService.get(setId, photoId));
   }

   @GetMapping("/{setId}/photos/before/{photoId}")
   public ResponseEntity<PhotoViewResponse> getBefore(@PathVariable Long setId,
                                                      @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetPhotoService.getBefore(setId, photoId));
   }

   @GetMapping("/{setId}/photos/after/{photoId}")
   public ResponseEntity<PhotoViewResponse> getAfter(@PathVariable Long setId,
                                                     @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetPhotoService.getAfter(setId, photoId));
   }
}
