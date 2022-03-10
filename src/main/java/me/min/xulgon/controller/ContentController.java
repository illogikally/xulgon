package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.service.CommentService;
import me.min.xulgon.service.FollowService;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contents")
@AllArgsConstructor
public class ContentController {
   private final CommentService commentService;
   private final FollowService followService;

   @GetMapping("/{contentId}/comments")
   public ResponseEntity<OffsetResponse<CommentResponse>> getCommentsByContent(@PathVariable Long contentId,
                                                                               OffsetRequest pageable) {
      return ResponseEntity.ok(commentService.getCommentsByContent(contentId, pageable));
   }

   @PutMapping("/{id}/follow")
   public ResponseEntity<Void> follow(@PathVariable Long id) {
      followService.followContent(id);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }

   @DeleteMapping("/{id}/unfollow")
   public ResponseEntity<Void> unfollow(@PathVariable Long id) {
      followService.unfollowContent(id);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }
}
