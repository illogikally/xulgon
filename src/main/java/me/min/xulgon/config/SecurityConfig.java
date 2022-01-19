package me.min.xulgon.config;

import lombok.AllArgsConstructor;
import me.min.xulgon.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
   private final JwtAuthenticationFilter jwtAuthenticationFilter;
   private final UserDetailsService userDetailsService;

   @Bean(BeanIds.AUTHENTICATION_MANAGER)
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http.cors().and().csrf().disable()
            .oauth2Login()
            .successHandler(this::successHandler)
            .and()
            .authorizeRequests()
            .antMatchers(
                  "/api/authentication/**",
                  "/oauth2/**",
                  "/login/**",
                  "/ws/**",
                  "/contents/**")
            .permitAll()
            .antMatchers(
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

   private void successHandler(HttpServletRequest request,
                               HttpServletResponse response,
                               Authentication authentication) {
      System.out.println(authentication);
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
