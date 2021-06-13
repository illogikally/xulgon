package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoResponse {
   private Long id;
   private UserDto user;
   private Long parentId;
   private String createdAt;
   private String body;
   private String url;
   private Boolean isReacted;
   private Float sizeRatio;
   private Integer reactionCount;
   private Integer commentCount;
   private Integer shareCount;
}
