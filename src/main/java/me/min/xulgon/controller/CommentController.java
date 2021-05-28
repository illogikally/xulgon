package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {
   private final CommentService commentService;

   @PostMapping
   public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest commentRequest) {
      return ResponseEntity.status(HttpStatus.OK).body(commentService.save(commentRequest));

   }

}
