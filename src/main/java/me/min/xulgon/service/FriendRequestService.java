package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.FriendRequestDto;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.mapper.FriendRequestMapper;
import me.min.xulgon.model.FriendRequest;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FriendRequestRepository;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class FriendRequestService {

   private final SimpMessagingTemplate simpMessagingTemplate;
   private final FriendRequestRepository friendRequestRepository;
   private final AuthenticationService authenticationService;
   private final UserRepository userRepository;
   private final FriendRequestMapper friendRequestMapper;
   private final PrincipalService principalService;
   private final NotificationService notificationService;
   private final FriendshipRepository friendshipRepository;

   public void save(Long requesteeId) {
      User requestee = userRepository.findById(requesteeId)
            .orElseThrow(UserNotFoundException::new);

      var isRequestExisted = friendRequestRepository.findByRequesterAndRequestee(
            authenticationService.getPrincipal(),
            requestee
      ).isPresent();

      var isAlreadyFriends = friendshipRepository.findByUsers(
            authenticationService.getPrincipal(),
            requestee
      ).isPresent();

      if (isRequestExisted || isAlreadyFriends) return;

      var request = friendRequestRepository.save(
            FriendRequest.builder()
                  .requester(authenticationService.getPrincipal())
                  .requestee(requestee)
                  .createdAt(Instant.now())
                  .build()
      );

      simpMessagingTemplate.convertAndSendToUser(
            requestee.getUsername(),
            "/queue/friend-request",
            friendRequestMapper.toDto(request)
      );

      notificationService.createNotification(
            principalService.getPrincipal(),
            null,
            null,
            null,
            requestee.getProfile(),
            requestee,
            NotificationType.FRIEND_REQUEST
      );
   }

   public void delete(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);

      friendRequestRepository.deleteByUsers(
            authenticationService.getPrincipal(),
            user
      );
   }


   @Transactional(readOnly = true)
   public List<FriendRequestDto> getRequestsByRequestee(Long requesteeId) {
      User requestee = userRepository.findById(requesteeId)
            .orElseThrow(UserNotFoundException::new);
      return friendRequestRepository.findAllByRequestee(requestee)
            .stream()
            .map(friendRequestMapper::toDto)
            .collect(Collectors.toList());
   }
}
