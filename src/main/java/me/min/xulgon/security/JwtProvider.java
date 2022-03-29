package me.min.xulgon.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtProvider {

   @Autowired
   private UserDetailsService userDetailsService;
   @Value("${jwt.signing-key}")
   private String key;
   @Value("${jwt.expiration-time-in-millis}")
   private Long jwtExpirationInMillis;

   public String generateToken(Authentication authentication) {
      return generateTokenWithUserName(authentication.getName());
   }

   public boolean validateToken(String jwt) {
      try {
         Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);
         return true;
      }
      catch (Exception ignored) {
         return false;
      }
   }

   public String getUsernameFromJwt(String token) {
      Claims claims = Jwts.parser()
            .setSigningKey(key)
            .parseClaimsJws(token)
            .getBody();

      return claims.getSubject();
   }

   public Authentication getAuthenticationFromJwt(String token) {
      UserDetails user = userDetailsService.loadUserByUsername(getUsernameFromJwt(token));
      var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      auth.setDetails(new WebAuthenticationDetailsSource());
      return auth;
   }

   public String generateTokenWithUserName(String username) {
      return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .signWith(SignatureAlgorithm.HS256, key)
            .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
            .compact();
   }

   public Long getJwtExpirationInMillis() {
      return jwtExpirationInMillis;
   }
}

