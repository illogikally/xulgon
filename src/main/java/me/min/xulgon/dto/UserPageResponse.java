package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserPageResponse {
   private Long id;
   private Long userId;
   private String workplace;
   private String school;
   private String hometown;
   private List<PhotoResponse> photos;
   private List<UserDto> friends;
}
