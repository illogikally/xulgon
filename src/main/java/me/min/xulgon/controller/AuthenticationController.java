package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationRequest;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authentication")
@AllArgsConstructor
public class AuthenticationController {
   private final AuthenticationService authenticationService;

   @PostMapping("/login")
   public AuthenticationResponse login(@RequestBody AuthenticationRequest authenticationRequest) {
      return authenticationService.login(authenticationRequest);
   }
}
