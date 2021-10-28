package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.*;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserProfileRepository;
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


@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
   private final JwtProvider jwtProvider;

   private final RefreshTokenService refreshTokenService;
   private final PhotoRepository photoRepository;
   private final AuthenticationManager authenticationManager;
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final VerificationTokenRepository verificationTokenRepository;
   private final UserProfileRepository userProfileRepository;

   public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
      Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                  authenticationRequest.getPassword())
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtProvider.generateToken(authentication);
      User loggedInUser = getLoggedInUser();
      return authResponseMapper(loggedInUser,
            token,
            refreshTokenService.generateRefreshToken().getToken());
   }

   private AuthenticationResponse authResponseMapper(User user,
                                                     String token,
                                                     String refreshToken) {
      return AuthenticationResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
            .profileId(user.getProfile().getId())
            .username(user.getUsername())
            .userFullName(user.getFullName())
            .avatarUrl(user.getProfile().getAvatar().getUrl())
            .build();
   }

   public void register(RegisterDto registerDto) {
      User user = User.builder()
            .id(null)
            .username(registerDto.getUsername())
            .password(passwordEncoder.encode(registerDto.getPassword()))
            .firstName(registerDto.getFirstName())
            .lastName(registerDto.getLastName())
            .createdAt(Instant.now())
            .enabled(true)
            .fullName(registerDto.getLastName() + " " + registerDto.getFirstName())
            .build();

      user = userRepository.save(user);

      Photo avatar = Photo.builder()
            .url("http://localhost:8080/contents/default-avatar.png")
            .createdAt(Instant.now())
            .user(user)
            .sizeRatio(1F)
            .type(ContentType.PHOTO)
            .privacy(Privacy.PUBLIC)
            .build();

      avatar = photoRepository.save(avatar);
      userProfileRepository.save(UserProfile.builder()
            .user(user)
            .avatar(avatar)
            .type(PageType.PROFILE)
            .name(user.getFullName())
            .build());
   }

   @Transactional(readOnly = true)
   public User getLoggedInUser() {
      return getLoggedInUser(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
   }

   public User getLoggedInUser(Object principal) {
      return userRepository
              .findByUsername(((org.springframework.security.core.userdetails.User) principal).getUsername())
              .orElseThrow(() -> new RuntimeException("User not found"));
   }

   public AuthenticationResponse refreshToken(RefreshTokenDto refreshTokenDto) {
      refreshTokenService.validateRefreshToken(refreshTokenDto.getToken());
      String token = jwtProvider.generateTokenWithUserName(refreshTokenDto.getUsername());
      User user = userRepository.findByUsername(refreshTokenDto.getUsername())
            .orElseThrow(RuntimeException::new);
      return authResponseMapper(user, token, refreshTokenDto.getToken());
   }
}
