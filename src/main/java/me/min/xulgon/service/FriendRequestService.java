package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.FriendRequestDto;
import me.min.xulgon.mapper.FriendRequestMapper;
import me.min.xulgon.model.FriendRequest;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.FriendRequestRepository;
import me.min.xulgon.repository.FriendshipRepository;
import me.min.xulgon.repository.UserRepository;
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

   private final FriendRequestRepository friendRequestRepository;
   private final FriendshipRepository friendshipRepository;
   private final AuthenticationService authenticationService;
   private final UserRepository userRepository;
   private final FriendRequestMapper friendRequestMapper;

   public void save(Long requesteeId) {
      User requestee = userRepository.findById(requesteeId)
            .orElseThrow(() -> new RuntimeException("User not found"));
      log.error("Requestee " + requesteeId + "/// resutertet " + authenticationService.getLoggedInUser().getId() );
      friendRequestRepository.save(FriendRequest.builder()
            .requester(authenticationService.getLoggedInUser())
            .requestee(requestee)
            .createdAt(Instant.now())
            .build());
   }

   public void delete(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

      friendRequestRepository.deleteByUsers(
            authenticationService.getLoggedInUser(),
            user
      );
   }


   @Transactional(readOnly = true)
   public List<FriendRequestDto> getRequestsByRequestee(Long requesteeId) {
      User requestee = userRepository.findById(requesteeId)
            .orElseThrow(() -> new RuntimeException("User not found"));
      return friendRequestRepository.findAllByRequestee(requestee)
            .stream()
            .map(friendRequestMapper::toDto)
            .collect(Collectors.toList());
   }
}
