package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.FriendshipStatus;

@Data
@Builder
public class UserProfileHeaderDto {
   private Long id;
   private String profileName;
   private FriendshipStatus friendshipStatus;
   private PhotoViewResponse avatar;
   private String coverPhotoUrl;
   private Boolean blocked;
   private Long userId;
   private String profileCoverUrl;
}
