package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.model.Follow;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class PageService {

   private final FollowRepository followRepository;
   private final PageRepository pageRepository;
   private final PrincipalService principalService;
   private final PhotoRepository photoRepository;
   private PhotoService photoService;
   private PhotoSetPhotoService photoSetPhotoService;
   private PhotoMapper photoMapper;

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

   public PhotoResponse uploadCoverPhoto(Long pageId,
                                         PhotoRequest request,
                                         MultipartFile multipartFile) {

      Page page = pageRepository.findById(pageId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoService.save(request, multipartFile);
      try {
         BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());
         photo.setDominantColorLeft(Util.getDominantColorLeft(bufferedImage));
         photo.setDominantColorRight(Util.getDominantColorRight(bufferedImage));
         photoRepository.save(photo);
      }
      catch (Exception ignored) {}

      photoSetPhotoService.bulkInsertUnique(page.getCoverPhotoSet(), List.of(photo));
      photoSetPhotoService.bulkInsertUnique(page.getPagePhotoSet(), List.of(photo));

      page.setCoverPhoto(photo);
      pageRepository.save(page);
      return photoMapper.toPhotoResponse(photo);
   }
}
