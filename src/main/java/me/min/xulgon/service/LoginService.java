package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationRequest;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.dto.RegisterDto;
import me.min.xulgon.model.Provider;
import me.min.xulgon.model.User;
import me.min.xulgon.model.UserInfo;
import me.min.xulgon.repository.UserInfoRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LoginService {

   private final AuthenticationManager authenticationManager;
   private final JwtProvider jwtProvider;
   private final AuthenticationService authService;
   private final RefreshTokenService refreshTokenService;
   private final PasswordEncoder passwordEncoder;
   private final UserRepository userRepository;
   private final UserInfoRepository userInfoRepository;

   public void register(RegisterDto registerDto) {
      Optional<User> existed = userRepository.findByUsername(registerDto.getUsername());
      if (existed.isPresent()) {
         throw new RuntimeException("Username existed!");
      }

      var userInfo = userInfoRepository.save(new UserInfo());
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
            .userInfo(userInfo)
            .fullName(registerDto.getLastName() + " " + registerDto.getFirstName())
            .build();
      authService.saveUser(user);
   }

   public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
      Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                  authenticationRequest.getPassword())
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);
      String token = jwtProvider.generateToken(authentication);
      User principal = authService.getPrincipal();
      return authService.authenticationResponseMapper(
            principal,
            token,
            refreshTokenService.generateRefreshToken().getToken()
      );
   }
}
