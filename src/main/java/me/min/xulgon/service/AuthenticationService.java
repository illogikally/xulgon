package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserPageRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.security.JwtProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;


@Service
@AllArgsConstructor
@Transactional
public class AuthenticationService {
   private final JwtProvider jwtProvider;
   private final RefreshTokenService refreshTokenService;
   private final PhotoRepository photoRepository;
   private final UserRepository userRepository;
   private final UserPageRepository userPageRepository;


   public AuthenticationResponse authenticationResponseMapper(User user,
                                                               String token,
                                                               String refreshToken) {
      Long exp = Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()).toEpochMilli();
      return AuthenticationResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .profileId(user.getUserPage().getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .expiresAt(exp)
            .username(user.getUsername())
            .userFullName(user.getFullName())
            .avatarUrl(user.getUserPage().getAvatar().getUrl())
            .build();
   }


   public User saveUser(User user) {
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
      UserPage page = userPageRepository.save(UserPage.builder()
            .user(user)
            .avatar(avatar)
            .type(PageType.PROFILE)
            .name(user.getFullName())
            .build());

      user.setUserPage(page);
      return user;
   }

   private User googleOauth2UserMapper(OAuth2AuthenticationToken auth) {
      OAuth2User principle = auth.getPrincipal();

      return User.builder()
            .id(null)
            .username(auth.getName() + "GOOGLE")
            .password(null)
            .firstName(principle.getAttribute("given_name"))
            .lastName(principle.getAttribute("family_name"))
            .createdAt(Instant.now())
            .provider(Provider.GOOGLE)
            .email(principle.getAttribute("email"))
            .enabled(true)
            .fullName(principle.getAttribute("name"))
            .build();
   }

   public User createUserFromOauth2(OAuth2AuthenticationToken auth) {
      switch (auth.getAuthorizedClientRegistrationId()) {
         case "google":
            return googleOauth2UserMapper(auth);
         default:
            throw new RuntimeException("Oauth2 Provider not found!");
      }
   }

   public AuthenticationResponse oauth2Login(OAuth2AuthenticationToken auth) {
      Provider provider = Provider.valueOf(auth.getAuthorizedClientRegistrationId().toUpperCase());
      String oauth2Name = auth.getName() + provider;
      User user = userRepository.findByUsername(oauth2Name)
            .orElseGet(() -> saveUser(createUserFromOauth2(auth)));
      String authToken = jwtProvider.generateTokenWithUserName(user.getUsername());

      return authenticationResponseMapper(
              user,
              authToken,
              refreshTokenService.generateRefreshToken().getToken()
      );
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
