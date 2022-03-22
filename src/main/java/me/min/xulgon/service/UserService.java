package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.exception.UserNotFoundException;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.GroupMemberRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserRepository;
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
   private final PostMapper postMapper;
   private final PostRepository postRepository;
   private final GroupMemberRepository groupMemberRepository;
   private final AuthenticationService authService;
   private final FriendshipService friendshipService;
   private final UserMapper userMapper;
   private BlockService blockService;
   private PhotoMapper photoMapper;

   @Transactional(readOnly = true)
   public List<UserDto> getFriends(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
      return friendshipService.getFriends(user).stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   public OffsetResponse<PostResponse> getGroupFeed(Pageable pageable) {
      User principal =  authService.getPrincipal();
      var posts = postRepository.getUserGroupFeed(
               principal.getId(),
               pageable.getPageSize() + 1,
               pageable.getOffset()
      );
      boolean hasNext = posts.size() > pageable.getOffset();
      var postResponses = posts
            .stream()
            .limit(pageable.getPageSize())
            .filter(blockService::filter)
            .map(postMapper::toDto)
            .collect(Collectors.toList());

      return OffsetResponse
            .<PostResponse>builder()
            .hasNext(hasNext)
            .offset(pageable.getOffset())
            .data(postResponses)
            .size(postResponses.size())
            .build();
   }

   @Transactional(readOnly = true)
   public List<GroupResponse> getJoinedGroups() {
      User principal = authService.getPrincipal();
      return groupMemberRepository.findAllByUser(principal)
            .stream()
            .map(GroupMember::getGroup)
            .map(groupMapper::toDto)
            .collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   public List<PostResponse> getNewsFeed(Pageable pageable) {
      User principal = authService.getPrincipal();
      return postRepository.getUserNewsFeed(
                  principal.getId(),
                  pageable.getPageSize(),
                  pageable.getOffset()
            ).stream()
            .map(postMapper::toDto)
            .collect(Collectors.toList());
   }

   public List<UserBasicDto> getBasicFriends(Long id) {
      User user = userRepository.findById(id)
            .orElseThrow(UserNotFoundException::new);
      return friendshipService.getFriends(user)
            .stream()
            .map(userMapper::toBasicDto)
            .collect(Collectors.toList());
   }

   @Transactional(readOnly = true)
   public Boolean isUserExisted(String username) {
      return userRepository.findByUsername(username).isPresent();
   }

   public PhotoResponse getAvatar(Long userId) {
      User user = userRepository.findById(userId)
            .orElseThrow(RuntimeException::new);
      return photoMapper.toPhotoResponse(user.getProfile().getAvatar());
   }
}
