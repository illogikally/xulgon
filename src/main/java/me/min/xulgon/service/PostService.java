package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PageableResponse;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import me.min.xulgon.util.LimPageable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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

   public PageableResponse<PostResponse> getPostsByPage(Long id, Pageable pageable) {
      Page page = pageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return page.getType() == PageType.GROUP
            ? getPostsByGroup(id, pageable) : getPostsByProfile(id, pageable);
   }

   @Transactional(readOnly = true)
   public PageableResponse<PostResponse> getPostsByProfile(Long profileId, Pageable pageable) {
      UserPage userPage = userPageRepository.findById(profileId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

      List<Post> posts = postRepository.getProfilePosts(
            userPage.getId(),
            authService.getPrincipal().getId(),
            pageable.getPageSize() + 1,
            pageable.getOffset()
      );

      Boolean hasNext = posts.size() > pageable.getPageSize();
      Privacy privacy = getPrivacy(userPage.getUser());
      List<PostResponse> postResponses = posts
            .stream()
            .limit(pageable.getPageSize())
            .peek(post -> post.setPhotos(
                  post.getPhotos()
                        .stream()
                        .filter(photo -> photo.getPrivacy().ordinal() <= privacy.ordinal())
                        .collect(Collectors.toList())))
            .map(postMapper::toDto)
            .collect(Collectors.toList());

      return PageableResponse
            .<PostResponse>builder()
            .data(postResponses)
            .hasNext(hasNext)
            .size(pageable.getPageSize())
            .offset(pageable.getOffset())
            .build();

   }

   @Transactional(readOnly = true)
   public PageableResponse<PostResponse> getPostsByGroup(Long groupId, Pageable pageable) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);

      User principal = authService.getPrincipal();

      boolean isMember = group.getMembers()
            .stream()
            .anyMatch(member -> member.getUser().getId().equals(principal.getId()));
      if (!group.getIsPrivate() || isMember) {
         int size = pageable.getPageSize();
         pageable = new LimPageable(size + 1, pageable.getOffset());
          var postResponses = postRepository.findAllByPageOrderByCreatedAtDesc(group, pageable)
               .stream()
               .filter(blockService::filter)
               .map(postMapper::toDto)
               .collect(Collectors.toList());
          boolean hasNext = postResponses.size() > size;
          return PageableResponse
                .<PostResponse>builder()
                .size(size)
                .hasNext(hasNext)
                .offset(pageable.getOffset())
                .data(postResponses.stream().limit(size).collect(Collectors.toList()))
                .build();
      }
      return PageableResponse.empty();
   }

//   public List<PostResponse> getPostsByGroup(Long groupId) {
//      return getPostsByGroup(groupId, Pageable.unpaged());
//   }


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
