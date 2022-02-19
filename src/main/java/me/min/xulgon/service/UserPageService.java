package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.mapper.PhotoViewMapper;
import me.min.xulgon.mapper.UserPageMapper;
import me.min.xulgon.model.FriendshipStatus;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.UserPage;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class UserPageService {

   private final UserPageRepository userPageRepository;
   private final UserPageMapper userPageMapper;
   private final StorageService storageService;
   private final FriendshipService friendshipService;
   private final PhotoViewMapper photoViewMapper;
   private final UserService userService;


   private final PhotoRepository photoRepository;

   public UserProfileResponse getUserProfile(Long id) {
      UserPage userPage = userPageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      return userPageMapper.toDto(userPage);
   }

   public List<UserDto> getFriends(Long id) {
      UserPage profile = userPageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return userService.getFriends(profile.getUser().getId());
   }

   public void updateAvatar(Long profileId, Long photoId) {
      UserPage userPage = userPageRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

      userPage.setAvatar(photo);
      userPageRepository.save(userPage);
   }

   public PhotoViewResponse uploadAvatar(Long profileId,
                                         PhotoRequest request,
                                         MultipartFile multipartFile) {
      UserPage profile = userPageRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("User Profile not found"));
      String name = storageService.store(multipartFile);
      request.setPageId(profileId);
      Photo photo = photoRepository.save(photoViewMapper.map(request, name));
      profile.setAvatar(photo);
      userPageRepository.save(profile);
      return photoViewMapper.toDto(photo);
   }

   public void updateCover(Long profileId, Long photoId) {
      UserPage userPage = userPageRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

      userPage.setCoverPhoto(photo);
      userPageRepository.save(userPage);
   }

   public PhotoViewResponse uploadCover(Long profileId,
                                        PhotoRequest request,
                                        MultipartFile multipartFile) {
      UserPage profile = userPageRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("User Profile not found"));
      String name = storageService.store(multipartFile);
      request.setPageId(profileId);
      Photo photo = photoRepository.save(photoViewMapper.map(request, name));
      profile.setCoverPhoto(photo);
      userPageRepository.save(profile);
      return photoViewMapper.toDto(photo);
   }

   public UserProfileHeaderDto getProfileHeader(Long id) {
      var userPage = userPageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));
      return UserProfileHeaderDto.builder()
            .id(userPage.getId())
            .friendshipStatus(friendshipService.getFriendshipStatus(userPage.getUser()))
            .userId(userPage.getUser().getId())
            .avatar(photoViewMapper.toDto(userPage.getAvatar()))
            .profileCoverUrl((userPage.getCoverPhoto().orElseGet(Photo::new)).getUrl())
            .profileName(userPage.getName())
            .build();
   }

//   public void updateProfile(Long id,
//                             MultipartFile multipartFile,
//                             UserProfileRequest request) {
//      UserProfile userProfile = userProfileRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Profile not found"));
//
//      if (request.getAvatarId() != null) {
//         Photo avatar = photoRepository.findById(request.getAvatarId())
//               .orElseThrow(() -> new RuntimeException("Photo not found"));
//         userProfile.setAvatar(avatar);
//      }
//
//   }


}
