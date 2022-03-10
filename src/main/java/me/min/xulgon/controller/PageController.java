package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.service.PageService;
import me.min.xulgon.service.PhotoService;
import me.min.xulgon.service.PostService;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pages")
@AllArgsConstructor
public class PageController {

   private final PostService postService;
   private final PhotoService photoService;
   private final PageService pageService;

   @GetMapping("/{id}/posts")
   public ResponseEntity<OffsetResponse<PostResponse>> getPosts(@PathVariable Long id,
                                                                OffsetRequest pageable) {
      return ResponseEntity.ok(postService.getPostsByPage(id, pageable));
   }

   @GetMapping("/{id}/photos")
   public ResponseEntity<List<PhotoResponse>> getPhotos(@PathVariable Long id) {
      return ResponseEntity.ok(photoService.getPhotosByPage(id));
   }

   @PostMapping("/{id}/follow")
   public ResponseEntity<Void> follow(@PathVariable Long id) {
      pageService.follow(id);
      return ResponseEntity.ok().build();
   }

   @DeleteMapping("/{id}/unfollow")
   public ResponseEntity<Void> unfollow(@PathVariable Long id) {
      pageService.unfollow(id);
      return ResponseEntity.ok().build();
   }

   @GetMapping("/{id}/photo-set-id")
   public ResponseEntity<Long> getPagePhotoSetId(@PathVariable Long id) {
      return ResponseEntity.ok(pageService.getPhotoSetId(id));
   }
}
