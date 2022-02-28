package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.dto.UserPageResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.BlockRepository;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.ContentService;
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
   private ContentService contentService;

   public UserPageResponse toDto(UserPage page) {
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

   private List<PhotoResponse> getPhotos(UserPage userPage) {
      return photoRepository.findAllByPageOrderByCreatedAtDesc(userPage, PageRequest.ofSize(9))
            .stream()
            .filter(contentService::privacyFilter)
            .map(photoMapper::toPhotoResponse)
            .collect(Collectors.toList());
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
