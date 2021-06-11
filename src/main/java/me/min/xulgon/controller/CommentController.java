package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/comments")
@AllArgsConstructor
public class CommentController {
   private final CommentService commentService;

   @PostMapping
   public ResponseEntity<CommentResponse> createComment(@RequestPart("commentRequest") CommentRequest commentRequest,
                                                        @RequestPart("photo") MultipartFile photo) {
      return ResponseEntity.status(HttpStatus.OK).body(commentService.save(commentRequest, photo));

   }

}
