package me.min.xulgon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class AuthenticationResponse {
   private String token;
   private String refreshToken;
   private Long expiresAt;
   private String userFullName;
   private String firstName;
   private String lastName;
   private String username;
   private Long userId;
   private Long profileId;
   private String avatarUrl;
}
