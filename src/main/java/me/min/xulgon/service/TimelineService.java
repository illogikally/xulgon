package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserProfileRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TimelineService {

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

      return posts;
   }
}
