package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.FriendshipStatus;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserPageResponse {
   private Long id;
   private Long userId;
   private PhotoViewResponse avatar;
   private String coverPhotoUrl;
   private String fullName;
   private String workplace;
   private String school;
   private String hometown;
   private FriendshipStatus friendshipStatus;
   private List<PhotoViewResponse> photos;
   private List<UserDto> friends;
   private Boolean blocked;
   private Boolean isBlocked;
}
