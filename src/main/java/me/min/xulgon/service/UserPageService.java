package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.mapper.UserPageMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserPageRepository;
import me.min.xulgon.util.Util;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserPageService {

   private final PrincipalService principalService;
   private final PhotoSetPhotoService photoSetPhotoService;
   private final UserPageRepository userPageRepository;
   private final UserPageMapper userPageMapper;
   private final FriendshipService friendshipService;
   private final PhotoService photoService;
   private final PhotoMapper photoMapper;
   private final BlockService blockService;
   private final FollowService followService;
   private final UserService userService;
   private final Environment environment;


   private final PhotoRepository photoRepository;

   public UserPageResponse getUserProfile(Long id) {
      UserPage userPage = userPageRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);

      return userPageMapper.toDto(userPage);
   }

   public List<UserDto> getFriends(Long id) {
      UserPage profile = userPageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return userService.getFriends(profile.getUser().getId());
   }

   public void changeAvatar(Long photoId) {
      User principal = principalService.getPrincipal();
      UserPage page = principal.getUserPage();

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);

      photo.setPrivacy(Privacy.PUBLIC);
      photoRepository.save(photo);
      photoSetPhotoService.bulkInsertUnique(page.getAvatarSet(), List.of(photo));
      page.setAvatar(photo);
      userPageRepository.save(page);
   }

   public PhotoResponse uploadAvatar(PhotoRequest request,
                                     MultipartFile multipartFile) {
      User principal = principalService.getPrincipal();
      UserPage page = principal.getUserPage();
      Photo photo = photoService.save(request, multipartFile);

      photoSetPhotoService.bulkInsertUnique(page.getPagePhotoSet(), List.of(photo));
      photoSetPhotoService.bulkInsertUnique(page.getAvatarSet(), List.of(photo));
      page.setAvatar(photo);
      userPageRepository.save(page);
      return photoMapper.toPhotoResponse(photo);
   }

   public void changeCoverPhoto(Long photoId) {
      User principal = principalService.getPrincipal();
      UserPage page = principal.getUserPage();

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);

      photo.setPrivacy(Privacy.PUBLIC);
      photoRepository.save(photo);
      photoSetPhotoService.bulkInsertUnique(page.getCoverPhotoSet(), List.of(photo));

      page.setCoverPhoto(photo);
      userPageRepository.save(page);
   }

   public PhotoResponse uploadCoverPhoto(PhotoRequest request,
                                         MultipartFile multipartFile) {

      User principal = principalService.getPrincipal();
      UserPage page = principal.getUserPage();
      Photo photo = photoService.save(request, multipartFile);

      photoSetPhotoService.bulkInsertUnique(page.getCoverPhotoSet(), List.of(photo));
      photoSetPhotoService.bulkInsertUnique(page.getPagePhotoSet(), List.of(photo));

      page.setCoverPhoto(photo);
      userPageRepository.save(page);
      return photoMapper.toPhotoResponse(photo);
   }

   public PageHeaderDto getPageHeader(Long id) {
      var page = userPageRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);
      var coverPhoto = page.getCoverPhoto();
      String coverPhotoUrl = "";
      if (coverPhoto != null) {
         coverPhotoUrl = Util.getThumbnailUrl(
               environment,
               coverPhoto.getThumbnailsMap().get(ThumbnailType.s900x900)
         );
      }

      return PageHeaderDto.builder()
            .id(page.getId())
            .friendshipStatus(friendshipService.getFriendshipStatus(page.getUser()))
            .userId(page.getUser().getId())
            .avatar(photoMapper.toPhotoResponse(page.getAvatar()))
            .isBlocked(blockService.isBlocked(page.getId()))
            .isFollow(followService.isFollow(page))
            .blocked(blockService.blocked(page.getUser()))
            .coverPhoto(photoMapper.toPhotoResponse(page.getCoverPhoto()))
            .avatarPhotoSetId(page.getAvatarSet().getId())
            .name(page.getName())
            .build();
   }
}
