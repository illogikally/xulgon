package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.dto.UserProfileResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.BlockRepository;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.FriendshipService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserPageMapper {

   private final AuthenticationService authenticationService;
   private final BlockRepository blockRepository;
   private final FriendshipService friendshipService;
   private final UserMapper userMapper;
   private final PhotoViewMapper photoViewMapper;
   private final PhotoRepository photoRepository;
   private final BlockService blockService;

   public UserProfileResponse toDto(UserPage profile) {
      if (profile == null) return null;

      return UserProfileResponse.builder()
            .id(profile.getId())
            .fullName(profile.getUser().getFullName())
            .userId(profile.getUser().getId())
            .avatar(photoViewMapper.toDto(profile.getAvatar()))
            .coverPhotoUrl((profile.getCoverPhoto().orElseGet(Photo::new)).getUrl())
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

   private List<PhotoViewResponse> getPhotos(UserPage userPage) {
      return photoRepository.findAllByPageOrderByCreatedAtDesc(userPage, PageRequest.ofSize(9))
            .stream()
            .map(photoViewMapper::toDto)
            .collect(Collectors.toList());
//      return photoRepository.findAllByPage(userPage)
//            .stream()
//            .sorted((photo1, photo2) -> (int) -(photo1.getCreatedAt().toEpochMilli()- photo2.getCreatedAt().toEpochMilli()))
//            .limit(9)
//            .map(photoViewMapper::toDto)
//            .collect(Collectors.toList());
   }

   private List<UserDto> getFriends(UserPage profile) {
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


   private boolean isBlocked(UserPage userPage) {
      return blockRepository.findByBlockerAndBlockee(userPage.getUser(),
            authenticationService.getPrincipal()).isPresent();
   }
}
