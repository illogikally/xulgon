package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.FriendshipService;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMapper {

   private final FriendshipService friendshipService;
   private final BlockService blockService;
   private final FollowRepository followRepository;
   private final AuthenticationService authService;

   public UserDto toDto(User user) {
      return UserDto.builder()
            .id(user.getId())
            .profileId(user.getUserPage().getId())
            .blocked(blockService.blocked(user))
            .friendshipStatus(friendshipService.getFriendshipStatus(user))
            .isFollow(isFollow(user))
            .avatarUrl(user.getUserPage().getAvatar().getUrl())
            .username(user.getFullName())
            .commonFriendCount(friendshipService.getCommonFriendCount(user))
            .hometown(user.getUserPage().getHometown())
            .workplace(user.getUserPage().getWorkplace())
            .school(user.getUserPage().getSchool())
            .build();
   }

   private Boolean isFollow(User user) {
      return followRepository.findByUserAndPage(authService.getLoggedInUser(), user.getUserPage())
            .isPresent();
   }
}
