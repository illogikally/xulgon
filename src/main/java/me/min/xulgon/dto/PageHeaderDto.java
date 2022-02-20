package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.FriendshipStatus;

@Data
@Builder
public class PageHeaderDto {
   private Long id;
   private String name;
   private FriendshipStatus friendshipStatus;
   private PhotoViewResponse avatar;
   private String coverPhoto;
   private Boolean blocked;
   private Long userId;
}
