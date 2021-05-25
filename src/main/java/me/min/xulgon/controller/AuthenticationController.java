package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationRequest;
import me.min.xulgon.dto.AuthenticationResponse;
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

   @GetMapping("/account/verify")
   public ResponseEntity<String> verifyAccount(@RequestParam String token) {
      authenticationService.verifyAccount(token);
      return new ResponseEntity<>("Account has been successfully activated", HttpStatus.OK);
   }

   @PostMapping("/token/delete")
   public ResponseEntity<String> logout(@Validated @RequestBody String refreshToken) {
      refreshTokenService.deleteRefreshToken(refreshToken);
      return ResponseEntity.status(HttpStatus.OK).body("Refresh token is deleted.");
   }
}
