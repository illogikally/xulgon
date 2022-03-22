package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.UserBasicDto;
import me.min.xulgon.dto.UserDto;
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

   private final UserInfoMapper userInfoMapper;
   private final FriendshipService friendshipService;
   private final BlockService blockService;
   private final FollowRepository followRepository;
   private final AuthenticationService authService;
   private final Environment env;

   public UserDto toDto(User user) {
      return UserDto.builder()
            .id(user.getId())
            .profileId(user.getProfile().getId())
            .blocked(blockService.blocked(user))
            .friendshipStatus(friendshipService.getFriendshipStatus(user))
            .isFollow(isFollow(user))
            .avatarUrl(Util.getPhotoUrl(env, user.getProfile().getAvatar()))
            .username(user.getFullName())
            .userInfo(userInfoMapper.toDto(user.getUserInfo()))
            .commonFriendCount(friendshipService.getCommonFriendCount(user))
            .hometown(user.getProfile().getHometown())
            .workplace(user.getProfile().getWorkplace())
            .school(user.getProfile().getSchool())
            .build();
   }


   private Boolean isFollow(User user) {
      return followRepository.findByFollowerAndPage(authService.getPrincipal(), user.getProfile())
            .isPresent();
   }

   public UserBasicDto toBasicDto(User user) {
      return UserBasicDto.builder()
            .avatarUrl(Util.getPhotoUrl(env, user.getProfile().getAvatar()))
            .username(user.getFullName())
            .profileId(user.getProfile().getId())
            .id(user.getId())
            .build();
   }
}
