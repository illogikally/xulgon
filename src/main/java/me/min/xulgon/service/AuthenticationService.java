package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.*;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserPageRepository;
import me.min.xulgon.repository.VerificationTokenRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;


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
   private final UserPageRepository userPageRepository;

   public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
      Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                  authenticationRequest.getPassword())
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtProvider.generateToken(authentication);
      User principal = getPrincipal();
      return authenticationResponseMapper(
            principal,
            token,
            refreshTokenService.generateRefreshToken().getToken()
      );
   }

   private AuthenticationResponse authenticationResponseMapper(User user,
                                                               String token,
                                                               String refreshToken) {
      var exp = Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()).toEpochMilli();
      return AuthenticationResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .expiresAt(exp)
            .profileId(user.getUserPage().getId())
            .username(user.getUsername())
            .userFullName(user.getFullName())
            .avatarUrl(user.getUserPage().getAvatar().getUrl())
            .build();
   }

   public void register(RegisterDto registerDto) {
      User user = User.builder()
            .id(null)
            .username(registerDto.getUsername())
            .password(passwordEncoder.encode(registerDto.getPassword()))
            .provider(Provider.LOCAL)
            .email(registerDto.getEmail())
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
      userPageRepository.save(UserPage.builder()
            .user(user)
            .avatar(avatar)
            .type(PageType.PROFILE)
            .name(user.getFullName())
            .build());
   }

   public void createUser(OAuth2AuthenticationToken auth) {
      String name = auth.getName();
      OAuth2User principle = auth.getPrincipal();
      Provider provider = Provider.valueOf(auth.getAuthorizedClientRegistrationId());

      User user = User.builder()
            .id(null)
            .username(null)
            .password(null)
            .firstName(principle.getAttribute("given_name"))
            .lastName(principle.getAttribute("family_name"))
            .createdAt(Instant.now())
            .provider(provider)
            .email(principle.getAttribute("email"))
            .enabled(true)
            .fullName(principle.getAttribute("name"))
            .build();


   }

   @Transactional(readOnly = true)
   public User getPrincipal() {
      return getPrincipal(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
   }

   public User getPrincipal(Object principal) {
      return userRepository
              .findByUsername(((org.springframework.security.core.userdetails.User) principal).getUsername())
              .orElseThrow(() -> new RuntimeException("User not found"));
   }

   public AuthenticationResponse refreshToken(RefreshTokenDto refreshTokenDto) {
      refreshTokenService.validateRefreshToken(refreshTokenDto.getToken());
      String token = jwtProvider.generateTokenWithUserName(refreshTokenDto.getUsername());
      User user = userRepository.findByUsername(refreshTokenDto.getUsername())
            .orElseThrow(RuntimeException::new);
      return authenticationResponseMapper(user, token, refreshTokenDto.getToken());
   }
}
