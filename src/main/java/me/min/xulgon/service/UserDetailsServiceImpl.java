package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;


@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
   private final UserRepository userRepository;

   @Override
   @Transactional(readOnly = true)
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found."));
      String password = user.getPassword() == null ? "oauth2User" : user.getPassword();
      return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            password,
            user.getEnabled(),
            true,
            true,
            true,
            Collections.singleton(new SimpleGrantedAuthority("USER"))
      );
   }
}
