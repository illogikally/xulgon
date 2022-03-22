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
   private Long createdAt;
   private String userAvatarUrl;
   private Boolean isRead;
   private String username;
   private UserBasicDto user;
   private Long conversationId;
}
