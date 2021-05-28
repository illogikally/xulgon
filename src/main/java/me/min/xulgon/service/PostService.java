package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PostService {
   private final PostRepository postRepository;
   private final PostMapper postMapper;
   private final AuthenticationService authenticationService;
   private final UserProfileRepository userProfileRepository;
   private final FriendshipRepository friendshipRepository;

   @Transactional(readOnly = true)
   public List<PostResponse> getPostsByProfile(Long profileId) {
      UserProfile userProfile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Page not found"));
      List<Post> posts = postRepository.findAllByPage(userProfile);
      User loggedInUser = authenticationService.getLoggedInUser();
      Privacy privacy = getPrivacy(loggedInUser, userProfile.getUser());

      return posts.stream()
            .filter(post -> post.getPrivacy().ordinal() <= privacy.ordinal())
            .peek(post -> post.setPhotos(post.getPhotos().stream()
                  .filter(photo -> photo.getPrivacy().ordinal() <= privacy.ordinal())
                  .collect(Collectors.toList())))
            .map(postMapper::toDto)
            .collect(Collectors.toList());

   }

   private Privacy getPrivacy(User userA, User userB) {
      return userA.equals(userB) ? Privacy.ME
            : friendshipRepository.findByUser(userA, userB).isPresent()
                  ? Privacy.FRIEND : Privacy.PUBLIC;
   }
}
