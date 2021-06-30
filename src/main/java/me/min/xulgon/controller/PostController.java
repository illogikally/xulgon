package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.service.ContentService;
import me.min.xulgon.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@AllArgsConstructor
@Slf4j
public class PostController {
   private final PostService postService;
   private final ContentService contentService;

   @PostMapping
   public ResponseEntity<PostResponse> save(@RequestPart("photos") List<MultipartFile> photos,
                                            @RequestPart("photoRequest") List<PhotoRequest> photoRequests,
                                            @RequestPart("postRequest") PostRequest postRequest) {


      return ResponseEntity.ok(postService.save(postRequest, photos, photoRequests));
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deletePost(@PathVariable Long id) {
      contentService.deleteContent(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @GetMapping("/{id}")
   public ResponseEntity<PostResponse> get(@PathVariable Long id) {
      return ResponseEntity.ok(postService.get(id));
   }
}
