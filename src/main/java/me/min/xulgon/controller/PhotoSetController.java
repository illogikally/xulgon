package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.service.PhotoSetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/photo-sets")
@AllArgsConstructor
public class PhotoSetController {

   private final PhotoSetService photoSetService;


   @GetMapping("/{setId}/photos/{photoId}")
   public ResponseEntity<PhotoViewResponse> getItemById(@PathVariable Long setId,
                                                        @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetService.getItem(setId, photoId));
   }

   @GetMapping("/{setId}/photos/before/{photoId}")
   public ResponseEntity<PhotoViewResponse> getItemBefore(@PathVariable Long setId,
                                                          @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetService.getItemBefore(setId, photoId));
   }

   @GetMapping("/{setId}/photos/after/{photoId}")
   public ResponseEntity<PhotoViewResponse> getItemAfter(@PathVariable Long setId,
                                                         @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetService.getItemAfter(setId, photoId));
   }
}
