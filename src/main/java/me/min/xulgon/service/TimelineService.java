package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.Group;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.GroupMemberRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserProfileRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TimelineService {

   private final GroupMemberRepository groupMemberRepository;
   private final FriendshipService friendshipService;
   private final AuthenticationService authenticationService;
   private final PostService postService;
   private final PostRepository postRepository;
   private final PostMapper postMapper;
   private final UserProfileRepository userProfileRepository;

   public List<PostResponse> getTimeline() {
      User user = authenticationService.getLoggedInUser();
      List<User> friends = friendshipService.getFriends(user);
      List<PostResponse> posts = new ArrayList<>();
      for (User friend : friends) {
         postService.getPostsByProfile(friend.getProfile().getId())
               .stream()
               .limit(3)
               .forEach(posts::add);
      }

      var members = groupMemberRepository.findAllByUser(user);
            members.stream()
            .map(GroupMember::getGroup)
            .map(group -> group.getPosts().stream()
                     .sorted((post1, post2) -> (int) (-post1.getCreatedAt().toEpochMilli() - post2.getCreatedAt().toEpochMilli()))
                     .limit(4)
                     .collect(Collectors.toList()))
            .flatMap(Collection::stream)
            .map(postMapper::toDto)
            .forEach(posts::add);

      return posts;
   }
}
