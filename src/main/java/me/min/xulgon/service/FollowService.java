package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.Page;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FollowService {

   private final PageRepository pageRepository;
   private final FollowRepository followRepository;
   private final UserRepository userRepository;
   private final AuthenticationService authService;


   public void deleteByPage(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(RuntimeException::new);

      followRepository.deleteByUserAndPage(authService.getPrincipal(), page);
   }
}
