package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.UserBasicDto;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.FriendshipService;
import me.min.xulgon.util.Util;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMapper {

   private final FriendshipService friendshipService;
   private final BlockService blockService;
   private final FollowRepository followRepository;
   private final AuthenticationService authService;
   private final Environment env;

   public UserDto toDto(User user) {
      Photo avatar = user.getUserPage().getAvatar();
      return UserDto.builder()
            .id(user.getId())
            .profileId(user.getUserPage().getId())
            .blocked(blockService.blocked(user))
            .friendshipStatus(friendshipService.getFriendshipStatus(user))
            .isFollow(isFollow(user))
            .avatarUrl(Util.getPhotoUrl(env, avatar))
            .username(user.getFullName())
            .commonFriendCount(friendshipService.getCommonFriendCount(user))
            .hometown(user.getUserPage().getHometown())
            .workplace(user.getUserPage().getWorkplace())
            .school(user.getUserPage().getSchool())
            .build();
   }


   private Boolean isFollow(User user) {
      return followRepository.findByUserAndPage(authService.getPrincipal(), user.getUserPage())
            .isPresent();
   }

   public UserBasicDto toBasicDto(User user) {
      return UserBasicDto.builder()
            .avatarUrl(Util.getPhotoUrl(env, user.getUserPage().getAvatar()))
            .username(user.getFullName())
            .profileId(user.getUserPage().getId())
            .id(user.getId())
            .build();
   }
}
