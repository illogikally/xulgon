package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
   private final FriendshipRepository friendshipRepository;
   private final PhotoService photoService;
   private final GroupRepository groupRepository;
   private final BlockService blockService;
   private final PageRepository pageRepository;

   public List<PostResponse> getPostsByPage(Long id, Pageable pageable) {
      Page page = pageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return page.getType() == PageType.GROUP
            ? getPostsByGroup(id) : getPostsByProfile(id, pageable);
   }

   public List<PostResponse> getPostsByPage(Long id) {
      return getPostsByPage(id, Pageable.unpaged());
   }

   @Transactional(readOnly = true)
   public List<PostResponse> getPostsByProfile(Long profileId, Pageable pageable) {
      UserProfile userProfile = userProfileRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      List<Post> posts = postRepository.findAllByPageOrderByCreatedAtDesc(userProfile, pageable);
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

   public List<PostResponse> getPostsByProfile(Long profileId) {
      return getPostsByProfile(profileId, Pageable.unpaged());
   }

   @Transactional(readOnly = true)
   public List<PostResponse> getPostsByGroup(Long groupId, Pageable pageable) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);

      User loggedInUser = authenticationService.getLoggedInUser();

      if (!group.getIsPrivate() || group.getMembers().stream().
            anyMatch(member -> member.getUser().getId().equals(loggedInUser.getId()))) {
         return postRepository.findAllByPageOrderByCreatedAtDesc(group, pageable).stream()
               .filter(blockService::filter)
               .map(postMapper::toDto)
               .collect(Collectors.toList());
      }
      return new ArrayList<>();
   }

   public List<PostResponse> getPostsByGroup(Long groupId) {
      return getPostsByGroup(groupId, Pageable.unpaged());
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
