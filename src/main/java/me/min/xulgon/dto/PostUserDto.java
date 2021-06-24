package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostUserDto {
   private Long id;
   private Long profileId;
   private String avatarUrl;
   private String username;

}
