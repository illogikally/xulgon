package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.mapper.PhotoViewMapper;
import me.min.xulgon.mapper.UserProfileMapper;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.UserProfile;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class UserProfileService {

   private final UserProfileRepository userProfileRepository;
   private final UserProfileMapper userProfileMapper;
   private final StorageService storageService;
   private final PhotoViewMapper photoViewMapper;
   private final UserService userService;


   private final PhotoRepository photoRepository;

   public UserProfileResponse getUserProfile(Long id) {
      UserProfile userProfile = userProfileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      return userProfileMapper.toDto(userProfile);
   }

   public List<UserDto> getFriends(Long id) {
      UserProfile profile = userProfileRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return userService.getFriends(profile.getUser().getId());
   }

   public void updateAvatar(Long profileId, Long photoId) {
      UserProfile userProfile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

      userProfile.setAvatar(photo);
      userProfileRepository.save(userProfile);
   }

   public PhotoViewResponse uploadAvatar(Long profileId,
                                         PhotoRequest request,
                                         MultipartFile multipartFile) {
      UserProfile profile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("User Profile not found"));
      String name = storageService.store(multipartFile);
      request.setPageId(profileId);
      Photo photo = photoRepository.save(photoViewMapper.map(request, name));
      profile.setAvatar(photo);
      userProfileRepository.save(profile);
      return photoViewMapper.toDto(photo);
   }

   public void updateCover(Long profileId, Long photoId) {
      UserProfile userProfile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

      userProfile.setCoverPhoto(photo);
      userProfileRepository.save(userProfile);
   }

   public PhotoViewResponse uploadCover(Long profileId,
                                        PhotoRequest request,
                                        MultipartFile multipartFile) {
      UserProfile profile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("User Profile not found"));
      String name = storageService.store(multipartFile);
      request.setPageId(profileId);
      Photo photo = photoRepository.save(photoViewMapper.map(request, name));
      profile.setCoverPhoto(photo);
      userProfileRepository.save(profile);
      return photoViewMapper.toDto(photo);
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
