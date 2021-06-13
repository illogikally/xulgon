package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.FriendRequestDto;
import me.min.xulgon.model.FriendRequest;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FriendRequestMapper {

   private final AuthenticationService authenticationService;
   private final FriendshipRepository friendshipRepository;

   public FriendRequestDto toDto(FriendRequest request) {
      return FriendRequestDto.builder()
            .id(request.getId())
            .requesterProfileId(request.getRequester().getProfile().getId())
            .requesterId(request.getRequester().getId())
            .requesterAvatarUrl(request.getRequester().getProfile().getAvatar().getUrl())
            .requesterName(getRequesterName(request))
            .createdAgo(MappingUtil.getCreatedAgo(request.getCreatedAt()))
            .commonFriendCount(getCommonFriendCount(request))
            .build();
   }

   private String getRequesterName(FriendRequest request) {
      return request.getRequester().getLastName() + " " + request.getRequester().getFirstName();
   }

   private Integer getCommonFriendCount(FriendRequest friendRequest) {
      User requester = friendRequest.getRequester();
      User requestee = friendRequest.getRequestee();

      List<User> requesterFriends = getFriends(requester);
      List<User> requesteeFriends = getFriends(requestee);


      requesterFriends.retainAll(requesteeFriends);
      return requesterFriends.size();
   }

   private List<User> getFriends(User user) {
      return friendshipRepository.findAllByUser(user).stream()
            .map(friendship -> {
               if (friendship.getUserA().equals(user)) {
                  return friendship.getUserB();
               }
               return friendship.getUserA();
            })
            .collect(Collectors.toList());
   }

}
