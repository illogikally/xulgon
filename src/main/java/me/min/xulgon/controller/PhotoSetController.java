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

   @GetMapping("/{setId}/photos/by-id/{photoId}")
   public ResponseEntity<PhotoViewResponse> getItemById(@PathVariable Long setId,
                                                       @PathVariable Long photoId) {

      return ResponseEntity.ok(photoSetService.getItemById(setId, photoId));
   }

   @GetMapping("/{setId}/photos/by-index/{index}")
   public ResponseEntity<PhotoViewResponse> getItemByIndex(@PathVariable Long setId,
                                                           @PathVariable Integer index) {

      return ResponseEntity.ok(photoSetService.getItemByIndex(setId, index));
   }
}
