package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.dto.RefreshTokenDto;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PhotoSetRepository;
import me.min.xulgon.repository.ProfileRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.security.JwtProvider;
import me.min.xulgon.util.Util;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;


@Service
@AllArgsConstructor
@Transactional
public class AuthenticationService {
   private final JwtProvider jwtProvider;
   private final RefreshTokenService refreshTokenService;
   private final UserRepository userRepository;
   private final PhotoSetRepository photoSetRepository;
   private final ProfileRepository profileRepository;
   private final Environment env;



   public AuthenticationResponse authenticationResponseMapper(User user,
                                                              String token,
                                                              String refreshToken) {
      Long exp = Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()).toEpochMilli();
      System.out.println("htere exp here" +  exp);
      System.out.println(Date.from(Instant.now()));
      System.out.println(Date.from(Instant.ofEpochMilli(exp)));

      return AuthenticationResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .userId(user.getId())
            .profileId(user.getProfile().getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .expiresAt(exp)
            .username(user.getUsername())
            .userFullName(user.getFullName())
            .avatarUrl(Util.getPhotoUrl(env, user.getProfile().getAvatar()))
            .build();
   }

   public User saveUser(User user) {
      user = userRepository.save(user);

      PhotoSet pagePhotoSet = PhotoSet.generate(SetType.PAGE);
      PhotoSet pageAvatarSet = PhotoSet.generate(SetType.AVATAR);
      PhotoSet pageCoverPhotoSet = PhotoSet.generate(SetType.COVER_PHOTO);
      pagePhotoSet = photoSetRepository.save(pagePhotoSet);
      pageAvatarSet = photoSetRepository.save(pageAvatarSet);
      pageCoverPhotoSet = photoSetRepository.save(pageCoverPhotoSet);
      Profile page = profileRepository.save(Profile.builder()
            .user(user)
            .type(PageType.PROFILE)
            .pagePhotoSet(pagePhotoSet)
            .avatarSet(pageAvatarSet)
            .coverPhotoSet(pageCoverPhotoSet)
            .name(user.getFullName())
            .build());

      user.setProfile(page);
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
              .orElseThrow(UserNotFoundException::new);
   }

   public AuthenticationResponse refreshToken(RefreshTokenDto refreshTokenDto) {
      refreshTokenService.validateRefreshToken(refreshTokenDto.getToken());
      String token = jwtProvider.generateTokenWithUserName(refreshTokenDto.getUsername());
      User user = userRepository.findByUsername(refreshTokenDto.getUsername())
            .orElseThrow(UserNotFoundException::new);
      return authenticationResponseMapper(user, token, refreshTokenDto.getToken());
   }
}
