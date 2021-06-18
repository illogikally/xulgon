package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.min.xulgon.model.FriendshipStatus;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
   private Long id;
   private Long profileId;
   private String avatarUrl;
   private String username;
   private Boolean blocked;
   private FriendshipStatus friendshipStatus;
   private Integer commonFriendCount;
   private String workplace;
   private String school;
   private String hometown;
}
