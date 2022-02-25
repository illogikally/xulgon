package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.service.CommentService;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@AllArgsConstructor
public class ContentController {
   private final CommentService commentService;

   @GetMapping("/{contentId}/comments")
   public ResponseEntity<OffsetResponse<CommentResponse>> getCommentsByContent(@PathVariable Long contentId,
                                                                               OffsetRequest pageable) {
      return ResponseEntity.ok(commentService.getCommentsByContent(contentId, pageable));
   }
}
