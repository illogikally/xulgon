package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class UserService {
   private final UserRepository userRepository;
   private final GroupMapper groupMapper;

   private final FollowRepository followRepository;
   private final PostService postService;
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

   @Transactional(readOnly = true)
   public List<GroupResponse> getJoinedGroups() {
      User loggedInUser = authService.getLoggedInUser();
      return groupMemberRepository.findAllByUser(loggedInUser)
            .stream()
            .map(GroupMember::getGroup)
            .map(groupMapper::toDto)
            .collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   public List<PostResponse> getNewsFeed(Pageable pageable) {
      User loggedInUser = authService.getLoggedInUser();
      return postRepository.getUserNewsFeed(loggedInUser.getId(),
                  pageable.getPageSize(),
                  pageable.getOffset())
            .stream()
            .filter(postService::privacyFilter)
            .map(postMapper::toDto)
            .collect(Collectors.toList());
   }

   public void unfollow(Long id) {
      User user = userRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      followRepository.deleteByUserAndPage(authService.getLoggedInUser(), user.getUserPage());
   }

}
