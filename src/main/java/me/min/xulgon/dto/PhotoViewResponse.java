package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.min.xulgon.model.ContentType;
import me.min.xulgon.model.PageType;
import me.min.xulgon.model.Privacy;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoViewResponse {
   private Long id;
   private UserDto user;
   private ContentType type;
   private Long parentId;
   private String createdAt;
   private String text;
   private List<PhotoResponse> photos;
   private Privacy privacy;
   private Boolean isReacted;
   private String pageName;
   private PageType pageType;
   private Boolean isFollow;
   private String pageId;
   private Integer reactionCount;
   private Integer commentCount;
   private Integer shareCount;

   private Boolean hasNext;
   private Boolean hasPrevious;
}
