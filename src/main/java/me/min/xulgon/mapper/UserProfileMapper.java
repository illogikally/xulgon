package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.dto.UserProfileResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.BlockRepository;
import me.min.xulgon.repository.FriendRequestRepository;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.FriendshipService;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserProfileMapper {

   private final FriendshipRepository friendshipRepository;
   private final FriendRequestRepository friendRequestRepository;
   private final AuthenticationService authenticationService;
   private final BlockRepository blockRepository;
   private final FriendshipService friendshipService;
   private final UserMapper userMapper;
   private final PhotoMapper photoMapper;
   private final PhotoRepository photoRepository;
   private final BlockService blockService;

   public UserProfileResponse toDto(UserProfile profile) {
      if (profile == null) return null;

      return UserProfileResponse.builder()
            .id(profile.getId())
            .firstName(profile.getUser().getFirstName())
            .lastName(profile.getUser().getLastName())
            .userId(profile.getUser().getId())
            .avatar(photoMapper.toDto(profile.getAvatar()))
            .coverPhotoUrl(profile.getCoverPhoto().getUrl())
            .workplace(profile.getWorkplace())
            .friends(getFriends(profile))
            .photos(getPhotos(profile))
            .school(profile.getSchool())
            .hometown(profile.getHometown())
            .friendshipStatus(friendshipService.getFriendshipStatus(profile.getUser()))
            .isBlocked(isBlocked(profile))
            .blocked(blockService.blocked(profile.getUser()))
            .build();

   }

   private List<PhotoResponse> getPhotos(UserProfile userProfile) {
      return photoRepository.findAllByPage(userProfile)
            .stream()
            .sorted((photo1, photo2) -> (int) -(photo1.getCreatedAt().toEpochMilli()- photo2.getCreatedAt().toEpochMilli()))
            .map(photoMapper::toDto)
            .limit(9)
            .collect(Collectors.toList());
   }

   private List<UserDto> getFriends(UserProfile profile) {
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


   private boolean isBlocked(UserProfile userProfile) {
      return blockRepository.findByBlockerAndBlockee(userProfile.getUser(),
            authenticationService.getLoggedInUser()).isPresent();
   }
}
