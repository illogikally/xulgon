package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.ContentType;
import me.min.xulgon.model.PageType;
import me.min.xulgon.model.Privacy;

import java.util.List;

@Data
@Builder
public class ContentDto {
   private Long id;
   private String text;
   private ContentType type;
   private String createdAt;
   private UserDto user;
   private Long pageId;
   private String pageName;
   private Privacy privacy;
   private PageType pageType;
   private Integer photoCount;
   private Long photoSetId;
   private List<PhotoResponse> photos;
}
