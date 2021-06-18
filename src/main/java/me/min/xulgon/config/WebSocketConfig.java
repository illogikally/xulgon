package me.min.xulgon.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.security.JwtProvider;
import me.min.xulgon.service.AuthenticationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

   private final JwtProvider jwtProvider;

   @Override
   public void registerStompEndpoints(StompEndpointRegistry registry) {
      registry.addEndpoint("/ws")
            .setAllowedOrigins("http://localhost:4200")
            .withSockJS();
   }

   @Override
   public void configureMessageBroker(MessageBrokerRegistry registry) {
      registry.setApplicationDestinationPrefixes("/app");
      registry.enableSimpleBroker( "/queue/chat");
      registry.setUserDestinationPrefix("/user");
   }

   @Override
   public void configureClientInboundChannel(ChannelRegistration registration) {
      registration.interceptors(new ChannelInterceptor() {

         @Override
         public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
            StompHeaderAccessor accessor =
                  MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
               String authToken = accessor.getFirstNativeHeader("X-Authorization");
               Authentication authentication =  jwtProvider.getAuthenticationFromJwt(authToken);
               SecurityContextHolder.getContext().setAuthentication(authentication);
               accessor.setUser(authentication);
            }

            return message;
         }
      });
   }
}