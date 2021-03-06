package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.dto.UserPageResponse;
import me.min.xulgon.model.PhotoSetPhoto;
import me.min.xulgon.model.Profile;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.BlockRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.ContentService;
import me.min.xulgon.service.FriendshipService;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ProfileMapper {

   private final AuthenticationService authenticationService;
   private final BlockRepository blockRepository;
   private final FriendshipService friendshipService;
   private final UserMapper userMapper;
   private final PhotoMapper photoMapper;
   private ContentService contentService;

   public UserPageResponse toDto(Profile page) {
      if (page == null) return null;

      return UserPageResponse.builder()
            .id(page.getId())
            .userId(page.getUser().getId())
            .workplace(page.getWorkplace())
            .friends(getFriends(page))
            .photos(getPhotos(page))
            .school(page.getSchool())
            .hometown(page.getHometown())
            .build();
   }

   private List<PhotoResponse> getPhotos(Profile userPage) {
      return userPage.getPagePhotoSet()
            .getPhotoSetPhotos()
            .stream()
            .map(PhotoSetPhoto::getPhoto)
            .filter(contentService::isPrivacyAdequate)
            .map(photoMapper::toPhotoResponse)
            .limit(9)
            .collect(Collectors.toList());
   }

   private List<UserDto> getFriends(Profile profile) {
      List<User> profileFriendList = friendshipService.getFriends(profile.getUser());
      return profileFriendList.stream()
            .map(user -> new AbstractMap.SimpleEntry<>(user,
                  friendshipService.getCommonFriendCount(user)))
            .sorted(Map.Entry.comparingByValue())
            .limit(9)
            .map(Map.Entry::getKey)
            .map(userMapper::toDto)
            .collect(Collectors.toList());
   }


   private boolean isBlocked(Profile userPage) {
      return blockRepository.findByBlockerAndBlockee(userPage.getUser(),
            authenticationService.getPrincipal()).isPresent();
   }
}
