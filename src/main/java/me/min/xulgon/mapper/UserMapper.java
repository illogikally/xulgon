package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.model.User;
import me.min.xulgon.service.BlockService;
import me.min.xulgon.service.FriendshipService;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMapper {

   private final FriendshipService friendshipService;
   private final BlockService blockService;

   public UserDto toDto(User user) {
      return UserDto.builder()
            .id(user.getId())
            .profileId(user.getProfile().getId())
            .blocked(blockService.blocked(user))
            .friendshipStatus(friendshipService.getFriendshipStatus(user))
            .avatarUrl(user.getProfile().getAvatar().getUrl())
            .username(user.getFullName())
            .commonFriendCount(friendshipService.getCommonFriendCount(user))
            .hometown(user.getProfile().getHometown())
            .workplace(user.getProfile().getWorkplace())
            .school(user.getProfile().getSchool())
            .build();
   }
}
