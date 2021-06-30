package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.ContentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
   private Long id;
   private Long parentId;
   private ContentType parentType;
   private String body;
   private UserDto user;
   private Boolean isReacted;
   private Long postId;
   private PhotoViewResponse photo;
   private String createdAgo;
   private Integer reactionCount;
   private Integer replyCount;
}
