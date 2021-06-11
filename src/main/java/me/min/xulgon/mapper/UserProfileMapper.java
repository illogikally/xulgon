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
import me.min.xulgon.service.FriendshipService;
import org.springframework.stereotype.Service;

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
   private final PhotoMapper photoMapper;
   private final PhotoRepository photoRepository;

   public UserProfileResponse toDto(UserProfile userProfile) {
      if (userProfile == null) return null;

      return UserProfileResponse.builder()
            .id(userProfile.getId())
            .firstName(userProfile.getUser().getFirstName())
            .lastName(userProfile.getUser().getLastName())
            .userId(userProfile.getUser().getId())
            .avatarUrl(userProfile.getAvatar().getUrl())
            .coverPhotoUrl(userProfile.getCoverPhoto().getUrl())
            .workplace(userProfile.getWorkplace())
            .friends(getFriends(userProfile))
            .photos(getPhotos(userProfile))
            .school(userProfile.getSchool())
            .hometown(userProfile.getHometown())
            .friendshipStatus(getFriendshipStatus(userProfile))
            .isBlocked(isBlocked(userProfile))
            .build();

   }

   private List<PhotoResponse> getPhotos(UserProfile userProfile) {
      return photoRepository.findAllByPage(userProfile)
            .stream()
            .map(photoMapper::toDto)
            .limit(9)
            .collect(Collectors.toList());
   }

   private List<UserDto> getFriends(UserProfile profile) {
      List<User> profileFriendList = friendshipService.getFriends(profile.getUser());
      Map<User, Integer> map = profileFriendList.stream()
            .collect(Collectors.toMap(user -> user, friendshipService::getCommonFriendCount));

//      var x = new ArrayList<UserDto>();
//      x.add(UserDto.builder().build());
//      return x;
      return map.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(9)
            .map(entry -> UserDto.builder()
                     .id(entry.getKey().getId())
                     .username(entry.getKey().getLastName() + " " + entry.getKey().getFirstName())
                     .commonFriendCount(entry.getValue())
                     .avatarUrl(entry.getKey().getAvatar().getUrl())
                     .build()
            )
            .collect(Collectors.toList());
   }


   private FriendshipStatus getFriendshipStatus(UserProfile userProfile) {
      FriendshipStatus status = null;
      User loggedInUser = authenticationService.getLoggedInUser();

      if (friendshipRepository.findByUsers(userProfile.getUser(),
            loggedInUser).isPresent()) {
         status = FriendshipStatus.FRIEND;
      }

      else if (friendRequestRepository.findByRequesterAndRequestee(loggedInUser,
            userProfile.getUser()).isPresent()) {
         status = FriendshipStatus.SEND;
      }
      else if (friendRequestRepository.findByRequesterAndRequestee(userProfile.getUser(),
            loggedInUser).isPresent()) {
         status = FriendshipStatus.RECEIVED;
      }
      else if (loggedInUser != userProfile.getUser()) {
         status = FriendshipStatus.NULL;
      }

      return status;
   }

   private boolean isBlocked(UserProfile userProfile) {
      return blockRepository.findByBlockerAndBlockee(userProfile.getUser(),
            authenticationService.getLoggedInUser()).isPresent();
   }
}
