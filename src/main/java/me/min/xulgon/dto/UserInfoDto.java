package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
   private Long userId;
   private String hometown;
   private String school;
   private String currentResidence;
   private String workplace;
   private String relationship;
}
