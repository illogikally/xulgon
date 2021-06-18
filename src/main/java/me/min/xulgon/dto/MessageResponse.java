package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
   private Long id;
   private String message;
   private String createdAgo;
   private Long userId;
   private String userAvatarUrl;
   private String username;
}
