package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
   private Long id;
   private String avatarUrl;
   private String username;
   private Integer commonFriendCount;
}
