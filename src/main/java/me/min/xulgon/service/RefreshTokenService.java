package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.RefreshToken;
import me.min.xulgon.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

   private final RefreshTokenRepository refreshTokenRepository;

   public RefreshToken generateRefreshToken() {
      RefreshToken refreshToken = new RefreshToken();
      refreshToken.setToken(UUID.randomUUID().toString());
      refreshToken.setCreatedAt(Instant.now());

      return refreshTokenRepository.save(refreshToken);
   }

   void validateRefreshToken(String token) {
      System.out.println(token);
      refreshTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Refresh token not found."));
   }

   public void deleteRefreshToken(String token) {
      refreshTokenRepository.deleteByToken(token);
   }
}
