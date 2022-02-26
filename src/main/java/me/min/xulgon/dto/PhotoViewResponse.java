package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.PageType;
import me.min.xulgon.model.Privacy;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoViewResponse {
   private Long id;
   private UserDto user;
   private Long parentId;
   private String createdAt;
   private String body;
   private String url;
   private Privacy privacy;
   private Boolean isReacted;
   private String pageName;
   private PageType pageType;
   private String pageId;
   private Integer reactionCount;
   private Integer commentCount;
   private Integer shareCount;

   private Boolean hasNext;
   private Boolean hasPrevious;
}
