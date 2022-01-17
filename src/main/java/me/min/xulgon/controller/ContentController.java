package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.service.CommentService;
import me.min.xulgon.util.LimPageable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
@AllArgsConstructor
public class ContentController {
   private final CommentService commentService;

   @GetMapping("/{contentId}/comments")
   public ResponseEntity<List<CommentResponse>> getCommentsByContent(@PathVariable Long contentId,
                                                                     LimPageable pageable) {
      return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByContent(contentId, pageable));
   }
}
