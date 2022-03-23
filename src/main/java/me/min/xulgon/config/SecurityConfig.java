package me.min.xulgon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.min.xulgon.dto.AuthenticationResponse;
import me.min.xulgon.repository.InMemoryRequestRepository;
import me.min.xulgon.security.JwtAuthenticationFilter;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   private JwtAuthenticationFilter jwtAuthenticationFilter;
   private UserDetailsService userDetailsService;
   private ObjectMapper mapper;
   private AuthenticationService authService;


   @Bean(BeanIds.AUTHENTICATION_MANAGER)
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.cors()
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .oauth2Login()
               .loginPage("/api/authentication/no-auth")
               .authorizationEndpoint()
               .authorizationRequestRepository(new InMemoryRequestRepository())
            .and()
            .successHandler(this::successHandler)
            .and()
            .authorizeRequests()
            .antMatchers(
                  "/api/authentication/**",
                  "/oauth2/**",
                  "/api/users/existed",
                  "/login/**",
                  "/ws/**",
                  "/contents/**",

                  "/v2/api-docs",
                  "/configuration/ui",
                  "/swagger-resources/**",
                  "/configuration/security",
                  "/swagger-ui.html",
                  "/webjars/**")
            .permitAll()
            .anyRequest()
            .authenticated();

      http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
   }

   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.setAllowedMethods(List.of("*"));
      config.setAllowedOriginPatterns(List.of("*"));
      config.setAllowedHeaders(List.of("*"));

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);
      return source;
   }

   private void successHandler(HttpServletRequest request,
                               HttpServletResponse response,
                               Authentication authentication) throws IOException {

      OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
      AuthenticationResponse authenticationResponse = authService.oauth2Login(auth);
      response.getWriter().write(mapper.writeValueAsString(authenticationResponse));
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }
}
