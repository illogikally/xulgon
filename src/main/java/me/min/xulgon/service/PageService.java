package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.Follow;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
@Transactional
public class PageService {

   private final FollowRepository followRepository;
   private final PageRepository pageRepository;
   private final PrincipalService principalService;


   @Transactional(readOnly = true)
   public Long getPhotoSetId(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);
      return page.getPagePhotoSet().getId();
   }

   public void follow(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);

      User principal = principalService.getPrincipal();
      var followOptional = followRepository.findByFollowerAndPage(principal, page);

      if (followOptional.isEmpty()) {
         followRepository.save(
               Follow.builder()
                     .page(page)
                     .follower(principal)
                     .createdAt(Instant.now())
                     .build()
         );
      }
   }

   public void unfollow(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);

      User principal = principalService.getPrincipal();
      followRepository.deleteByFollowerAndPage(principal, page);
   }
}
