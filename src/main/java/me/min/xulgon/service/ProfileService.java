package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.mapper.UserInfoMapper;
import me.min.xulgon.mapper.ProfileMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserInfoRepository;
import me.min.xulgon.repository.ProfileRepository;
import me.min.xulgon.util.Util;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class ProfileService {

   private final PrincipalService principalService;
   private final PhotoSetPhotoService photoSetPhotoService;
   private final ProfileRepository profileRepository;
   private final ProfileMapper profileMapper;
   private final FriendshipService friendshipService;
   private final PhotoService photoService;
   private final PhotoMapper photoMapper;
   private final BlockService blockService;
   private final FollowService followService;
   private final UserService userService;
   private final Environment environment;


   private final PhotoRepository photoRepository;
   private UserInfoMapper userInfoMapper;
   private UserInfoRepository userInfoRepository;

   public UserPageResponse getUserProfile(Long id) {
      Profile userPage = profileRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);

      return profileMapper.toDto(userPage);
   }

   public List<UserDto> getFriends(Long id) {
      Profile profile = profileRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return userService.getFriends(profile.getUser().getId());
   }

   public void changeAvatar(Long photoId) {
      User principal = principalService.getPrincipal();
      Profile page = principal.getProfile();

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);

      photo.setPrivacy(Privacy.PUBLIC);
      photoRepository.save(photo);
      photoSetPhotoService.bulkInsertUnique(page.getAvatarSet(), List.of(photo));
      page.setAvatar(photo);
      profileRepository.save(page);
   }

   public PhotoResponse uploadAvatar(Long pageId,
                                     PhotoRequest request,
                                     MultipartFile multipartFile) {
      Profile page = profileRepository.findById(pageId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoService.save(request, multipartFile);

      photoSetPhotoService.bulkInsertUnique(page.getPagePhotoSet(), List.of(photo));
      photoSetPhotoService.bulkInsertUnique(page.getAvatarSet(), List.of(photo));
      page.setAvatar(photo);
      profileRepository.save(page);
      return photoMapper.toPhotoResponse(photo);
   }

   public void changeCoverPhoto(Long photoId) {
      User principal = principalService.getPrincipal();
      Profile page = principal.getProfile();

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);

      photo.setPrivacy(Privacy.PUBLIC);
      photoRepository.save(photo);
      photoSetPhotoService.bulkInsertUnique(page.getCoverPhotoSet(), List.of(photo));

      page.setCoverPhoto(photo);
      profileRepository.save(page);
   }


   public PageHeaderDto getPageHeader(Long id) {
      var page = profileRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);
      var coverPhoto = page.getCoverPhoto();
      String coverPhotoUrl = "";
      if (coverPhoto != null) {
         coverPhotoUrl = Util.getPhotoUrl(
               environment,
               coverPhoto
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

   public UserInfoDto getUserInfo(Long profileId) {
      Profile profile = profileRepository.findById(profileId)
            .orElseThrow(RuntimeException::new);
      return userInfoMapper.toDto(profile.getUser().getUserInfo());
   }

   public UserInfoDto saveUserInfo(Long profileId, UserInfoDto dto) {
      Profile profile = profileRepository.findById(profileId)
            .orElseThrow(RuntimeException::new);
      UserInfo userInfo = userInfoMapper.toUserInfo(dto, profile.getUser());
      return userInfoMapper.toDto(userInfoRepository.save(userInfo));
   }
}
