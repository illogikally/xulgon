package me.min.xulgon.dto;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserProfileRequest {
   private Long avatarId;
   private Long coverPhotoId;
}
