package me.min.xulgon.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileHeaderDto {
   private Long id;
   private String profileName;
   private PhotoViewResponse avatar;
   private String profileCoverUrl;
}
