package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PostService {

   private final PostRepository postRepository;
   private final PostMapper postMapper;
   private final AuthenticationService authService;
   private final UserPageRepository userPageRepository;
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
      UserPage userPage = userPageRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      List<Post> posts = postRepository.findAllByPageOrderByCreatedAtDesc(userPage, pageable);
      Privacy privacy = getPrivacy(userPage.getUser());

      return posts.stream()
            .filter(this::privacyFilter)
            .peek(post -> post.setPhotos(
                  post.getPhotos()
                        .stream()
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

      User principal = authService.getPrincipal();

      if (!group.getIsPrivate() || group.getMembers().stream().
            anyMatch(member -> member.getUser().getId().equals(principal.getId()))) {
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


   public PostResponse get(Long id) {
      return postRepository.findById(id)
            .filter(this::groupFilter)
            .filter(this::privacyFilter)
            .map(postMapper::toDto)
            .orElse(null);
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

   public boolean privacyFilter(Post post) {
      Privacy privacy = getPrivacy(post.getUser());
      return post.getPrivacy().ordinal() <= privacy.ordinal();
   }

   public List<String> getAllowedPrivacyList(Post post) {
      Privacy privacy = getPrivacy(post.getUser());
      return Arrays.stream(Privacy.values())
            .filter(p -> p.ordinal() <= privacy.ordinal())
            .map(Enum::toString)
            .collect(Collectors.toList());
   }

   public boolean groupFilter(Post post) {
      Optional<Group> groupOptional =  groupRepository.findById(post.getPage().getId());
      if (groupOptional.isPresent()) {
         Group group = groupOptional.get();
         if (group.getIsPrivate()) {
            return group.getMembers()
                  .stream()
                  .anyMatch(member -> member.getUser().equals(authService.getPrincipal()));
         }
      }
      return true;
   }

   private Privacy getPrivacy(User user) {
      User me = authService.getPrincipal();
      return me.equals(user) ? Privacy.ME
            : friendshipRepository.findByUsers(me, user).isPresent()
                  ? Privacy.FRIEND : Privacy.PUBLIC;
   }
}
