package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserBasicDto {
   private Long id;
   private String username;
   private String avatarUrl;
}
