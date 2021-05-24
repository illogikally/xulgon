package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationRequest;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@AllArgsConstructor
@Transactional
public class AuthenticationService {
   private final JwtProvider jwtProvider;
   private final AuthenticationManager authenticationManager;

   public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
      Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtProvider.generateToken(authentication);
      return AuthenticationResponse.builder()
            .token(token)
            .refreshToken("xxhaha")
            .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
            .username(authenticationRequest.getUsername())
            .build();
   }
}
