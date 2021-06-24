package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.service.PhotoService;
import me.min.xulgon.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pages")
@AllArgsConstructor
public class PageController {

   private final PostService postService;
   private final PhotoService photoService;

   @GetMapping("/{id}/posts")
   public ResponseEntity<List<PostResponse>> getPosts(@PathVariable Long id) {
      return ResponseEntity.ok(postService.getPostsByPage(id));
   }

   @GetMapping("/{id}/photos")
   public ResponseEntity<List<PhotoResponse>> getPhotos(@PathVariable Long id) {
      return ResponseEntity.ok(photoService.getPhotosByPage(id));
   }
}
