package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.Privacy;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
   private Long id;
   private Long pageId;
   private Boolean isReacted;
   private String createdAt;
   private UserDto user;
   private Privacy privacy;
   private String body;
   private Integer reactionCount;
   private Integer commentCount;
   private Integer shareCount;
   private List<CommentResponse> comments;
   private List<PhotoResponse> photos;
   private PostResponse sharedPost;
}
