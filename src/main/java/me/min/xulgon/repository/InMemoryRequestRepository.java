package me.min.xulgon.repository;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class InMemoryRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
   private final Map<String, OAuth2AuthorizationRequest> cache = new HashMap<>();

   @Override
   public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
      String state = request.getParameter("state");
      System.out.println("called load" + state);
      return state == null ? null : removeAuthorizationRequest(request);
   }

   @Override
   public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
      String state = authorizationRequest.getState();
      System.out.println("called save" + state);
      cache.put(state, authorizationRequest);
   }

   @Override
   public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
      String state = request.getParameter("state");
      return state == null ? null : cache.remove(state);
   }

}
