package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {
   private Long id;
   private Long pageId;
   private Long userId;
   private String username;
   private String createdAt;
   private String body;
   private Integer reactionCount;
   private Integer commentCount;
   private Integer shareCount;
}
