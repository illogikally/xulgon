package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/page/")
@AllArgsConstructor
public class ProfileController {
   private final PostService postService;

   @GetMapping("/{pageId}/posts")
   public ResponseEntity<List<PostResponse>> getPostsByPage(@PathVariable Long pageId) {
       return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByProfile(pageId));
   }

}
