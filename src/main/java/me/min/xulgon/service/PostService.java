package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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
   private final CommentService commentService;
   private final FriendshipRepository friendshipRepository;
   private final PhotoService photoService;
   private final GroupRepository groupRepository;
   private final BlockService blockService;
   private final PageRepository pageRepository;

   public List<PostResponse> getPostsByPage(Long id) {
      Page page = pageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      if (page.getType() == PageType.GROUP) {
         return getPostsByGroup(id);
      }
      return getPostsByProfile(id);
   }

   @Transactional(readOnly = true)
   public List<PostResponse> getPostsByProfile(Long profileId) {
      UserProfile userProfile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      List<Post> posts = postRepository.findAllByPageOrderByCreatedAtDesc(userProfile);
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

   @Transactional(readOnly = true)
   public List<PostResponse> getPostsByGroup(Long groupId) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);

      User loggedInUser = authenticationService.getLoggedInUser();

      if (!group.getIsPrivate() || group.getMembers().stream().
            anyMatch(member -> member.getUser().getId().equals(loggedInUser.getId()))) {
         return postRepository.findAllByPageOrderByCreatedAtDesc(group).stream()
               .filter(blockService::filter)
               .map(postMapper::toDto)
               .collect(Collectors.toList());
      }
      return new ArrayList<>();
   }

   public PostResponse save(PostRequest postRequest,
                            List<MultipartFile> photos,
                            List<PhotoRequest> photoRequests) {
      Post savedPost = postRepository.save(postMapper.map(postRequest));
      photoRequests.forEach(photoRequest -> photoRequest.setParentId(savedPost.getId()));

      List<Photo> savedPhotos = new ArrayList<>();
      for (int i = 0; i < Math.min(photos.size(), photoRequests.size()); ++i) {
         savedPhotos.add(photoService.save(photoRequests.get(i), photos.get(i)));
      }

      savedPost.setPhotos(savedPhotos);
      return postMapper.toDto(savedPost);
   }

   private Privacy getPrivacy(User userA, User userB) {
      return userA.equals(userB) ? Privacy.ME
            : friendshipRepository.findByUsers(userA, userB).isPresent()
                  ? Privacy.FRIEND : Privacy.PUBLIC;
   }
}
