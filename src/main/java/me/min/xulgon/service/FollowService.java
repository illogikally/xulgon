package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Follow;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FollowService {

   private final PageRepository pageRepository;
   private final FollowRepository followRepository;
   private final ContentRepository contentRepository;
   private final AuthenticationService authService;

   public void unfollowPage(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);

      followRepository.deleteByFollowerAndPage(authService.getPrincipal(), page);
   }

   public void unfollowContent(Long contentId) {
      Content content = contentRepository.findById(contentId)
            .orElseThrow(ContentNotFoundException::new);

      followRepository.deleteByFollowerAndContent(authService.getPrincipal(), content);
   }

   public void followPage(Long pageId) {
      User principal = authService.getPrincipal();
      Page page = pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);

      boolean isFollow = followRepository.findByFollowerAndPage(principal, page).isPresent();
      if (isFollow) return;

      followRepository.save(
            Follow.builder()
                  .page(page)
                  .follower(principal)
                  .build()
      );
   }

   public void followContent(Long contentId) {
      User principal = authService.getPrincipal();
      Content content = contentRepository.findById(contentId)
            .orElseThrow(ContentNotFoundException::new);

      boolean isFollow = followRepository.findByFollowerAndContent(principal, content).isPresent();
      if (isFollow) return;

      followRepository.save(
            Follow.builder()
                  .content(content)
                  .follower(principal)
                  .build()
      );
   }

   public Boolean isFollow(Content content) {
      return followRepository.findByFollowerAndContent(authService.getPrincipal(), content).isPresent();
   }

   public boolean isFollow(Page page) {
      return followRepository.findByFollowerAndPage(authService.getPrincipal(), page).isPresent();
   }
}
