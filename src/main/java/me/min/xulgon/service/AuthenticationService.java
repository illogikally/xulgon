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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.UUID;


@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {
   private final JwtProvider jwtProvider;
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
      return AuthenticationResponse.builder()
            .token(token)
            .refreshToken("xxhaha")
            .userId(loggedInUser.getId())
            .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
            .profileId(loggedInUser.getProfile().getId())
            .username(loggedInUser.getFirstName())
            .fullname(loggedInUser.getFullName())
            .avatarUrl(getAvatarUrl(loggedInUser))
            .build();
   }

   private String getAvatarUrl(User user) {
      return user.getProfile().getAvatar() == null ? null
            : user.getProfile().getAvatar().getUrl();
   }
   public void signUp(SignupRequest signupRequest) {
      User user = User.builder()
            .username(signupRequest.getUsername())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .firstName(signupRequest.getFirstName())
            .lastName(signupRequest.getLastName())
            .createdAt(Instant.now())
            .enabled(true)
            .build();

      userRepository.save(user);
      userProfileRepository.save(UserProfile.builder().user(user).build());

      String token = generateVerificationToken(user);
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

   public boolean verifyAccount(String token) {
      VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Token not found."));
      return !verificationToken.getExpiryDate().isAfter(Instant.now());
   }

   @Transactional(readOnly = true)
   public User getLoggedInUser() {
      return getLoggedInUser(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
   }

   public User getLoggedInUser(Object principal) {
      return userRepository.findByUsername(((org.springframework.security.core.userdetails.User) principal).getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
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
