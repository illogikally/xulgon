package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationRequest;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.dto.RefreshTokenDto;
import me.min.xulgon.dto.RegisterDto;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
@AllArgsConstructor
public class AuthenticationController {
   private final AuthenticationService authenticationService;
   private final RefreshTokenService refreshTokenService;

   @PostMapping("/token/retrieve")
   public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
      return authenticationService.login(authenticationRequest);
   }

   @PostMapping("/token/delete")
   public ResponseEntity<String> logout(@Validated @RequestBody String refreshToken) {
      refreshTokenService.deleteRefreshToken(refreshToken);
      return ResponseEntity.status(HttpStatus.OK).body("Refresh token is deleted.");
   }

   @PostMapping("/account/register")
   public ResponseEntity<Void> register(@RequestBody RegisterDto registerDto) {
      authenticationService.register(registerDto);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }

   @PostMapping("/token/refresh")
   public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenDto refreshTokenDto) {
      var kk = authenticationService.refreshToken(refreshTokenDto);
      System.out.println(kk);
      return ResponseEntity.ok(kk);
   }
}
