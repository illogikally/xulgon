package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.dto.UserPageResponse;
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
   private final PhotoMapper photoMapper;
   private final PhotoRepository photoRepository;
   private final BlockService blockService;

   public UserPageResponse toDto(UserPage page) {
      if (page == null) return null;

      return UserPageResponse.builder()
            .id(page.getId())
            .fullName(page.getUser().getFullName())
            .userId(page.getUser().getId())
            .avatar(photoMapper.toPhotoViewResponse(page.getAvatar()))
            .coverPhotoUrl(photoMapper.getUrl(page.getCoverPhoto()))
            .workplace(page.getWorkplace())
            .friends(getFriends(page))
            .photos(getPhotos(page))
            .school(page.getSchool())
            .hometown(page.getHometown())
            .friendshipStatus(friendshipService.getFriendshipStatus(page.getUser()))
            .isBlocked(isBlocked(page))
            .blocked(blockService.blocked(page.getUser()))
            .build();

   }

   private List<PhotoViewResponse> getPhotos(UserPage userPage) {
      return photoRepository.findAllByPageOrderByCreatedAtDesc(userPage, PageRequest.ofSize(9))
            .stream()
            .map(photoMapper::toPhotoViewResponse)
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
