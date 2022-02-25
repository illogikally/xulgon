package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrincipalService {
   private final UserRepository userRepository;

   public User getPrincipal() {
      var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      return userRepository
            .findByUsername(((org.springframework.security.core.userdetails.User) principal).getUsername())
            .orElseThrow(UserNotFoundException::new);
   }
}
