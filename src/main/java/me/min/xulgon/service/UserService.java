package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
   private final UserRepository userRepository;
   private final PostMapper postMapper;
   private final PostRepository postRepository;
   private final GroupMemberRepository groupMemberRepository;
   private final GroupRepository groupRepository;
   private final AuthenticationService authService;
   private final FriendshipService friendshipService;
   private final UserMapper userMapper;

   @Transactional(readOnly = true)
   public List<UserDto> getFriends(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
      return friendshipService.getFriends(user).stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   public List<PostResponse> getGroupFeed(Pageable pageable) {
      User loggedInUser =  authService.getLoggedInUser();
      return postRepository.getUserGroupFeed(loggedInUser.getId(),
            pageable.getPageSize(),
            pageable.getOffset())
            .stream()
            .map(postMapper::toDto)
            .collect(Collectors.toList());
   }

}
