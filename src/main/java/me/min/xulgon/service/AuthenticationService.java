package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationRequest;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.dto.SignupRequest;
import me.min.xulgon.model.VerificationToken;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.VerificationTokenRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;


@Service
@AllArgsConstructor
@Transactional
public class AuthenticationService {
   private final JwtProvider jwtProvider;
   private final AuthenticationManager authenticationManager;
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final VerificationTokenRepository verificationTokenRepository;

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

   public void signUp(SignupRequest signupRequest) {
      User user = User.builder()
            .username(signupRequest.getUsername())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .email(signupRequest.getEmail())
            .firstName(signupRequest.getFirstName())
            .lastName(signupRequest.getLastName())
            .createdAt(Instant.now())
            .enabled(true)
            .build();

      userRepository.save(user);

      String token = generateVerificationToken(user);
   }

   public boolean verifyAccount(String token) {
      VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found."));
      return !verificationToken.getExpiryDate().isAfter(Instant.now());
   }

   private String generateVerificationToken(User user) {
      String token = UUID.randomUUID().toString();
      VerificationToken verificationToken = VerificationToken.builder()
            .token(token)
            .user(user)
            .expiryDate(Instant.now().plusSeconds(3600L))
            .build();

      verificationTokenRepository.save(verificationToken);
      return token;
   }

}
