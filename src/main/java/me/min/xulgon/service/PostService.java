package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.model.PhotoSet;
import me.min.xulgon.repository.*;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
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
   private final PhotoSetPhotoRepository photoSetPhotoRepository;
   private final BlockService blockService;
   private final PhotoSetService photoSetService;
   private final PageRepository pageRepository;
   private final PhotoSetRepository photoSetRepository;

   public OffsetResponse<PostResponse> getPostsByPage(Long id, OffsetRequest pageable) {
      Page page = pageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return page.getType() == PageType.GROUP
            ? getPostsByGroup(id, pageable) : getPostsByProfile(id, pageable);
   }

   @Transactional(readOnly = true)
   public OffsetResponse<PostResponse> getPostsByProfile(Long profileId, OffsetRequest pageable) {
      UserPage userPage = userPageRepository.findById(profileId)
            .orElseThrow(PageNotFoundException::new);

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

      return OffsetResponse
            .<PostResponse>builder()
            .data(postResponses)
            .hasNext(hasNext)
            .size(pageable.getPageSize())
            .offset(pageable.getOffset())
            .build();

   }

   @Transactional(readOnly = true)
   public OffsetResponse<PostResponse> getPostsByGroup(Long groupId, OffsetRequest pageable) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(PageNotFoundException::new);

      User principal = authService.getPrincipal();

      boolean isMember = group.getMembers()
            .stream()
            .anyMatch(member -> member.getUser().equals(principal));

      if (!group.getIsPrivate() || isMember) {
          var posts
                = postRepository.findAllByPageOrderByCreatedAtDesc(group, pageable.sizePlusOne());
          boolean hasNext = posts.size() > pageable.getPageSize();
          var postResponses = posts
                .stream()
                .limit(pageable.getPageSize())
                .filter(blockService::filter)
                .map(postMapper::toDto)
                .collect(Collectors.toList());

          return OffsetResponse
                .<PostResponse>builder()
                .size(postResponses.size())
                .hasNext(hasNext)
                .offset(pageable.getOffset())
                .data(postResponses)
                .build();
      }
      return OffsetResponse.empty();
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
      Page page = pageRepository.findById(postRequest.getPageId())
            .orElseThrow(PageNotFoundException::new);

      PhotoSet postPhotoSet = PhotoSet.generate(SetType.POST);
      postPhotoSet = photoSetRepository.save(postPhotoSet);
      Post savedPost = postRepository.save(postMapper.map(postRequest, postPhotoSet));
      List<Photo> savedPhotos = new ArrayList<>();
      int photosLength = Math.min(photos.size(), photoRequests.size());
      if (photosLength > 0) {
         photoRequests.forEach(request -> request.setParentId(savedPost.getId()));
         for (int i = 0; i < photosLength; ++i) {
            Photo photo = photoService.save(photoRequests.get(i), photos.get(i));

            photoSetService.insertUniqueToPhotoSet(postPhotoSet, photo);
//            PhotoSetPhoto postSet = PhotoSetPhoto.builder()
//                  .photoSet(postPhotoSet)
//                  .photo(photo)
//                  .hasNext(i != photosLength - 1)
//                  .hasPrevious(i != 0);
//                  .createdAt(Instant.now())
//                  .build();

            photoSetService.insertUniqueToPhotoSet(page.getPagePhotoSet(), photo);
//            PhotoSetPhoto pageSet = PhotoSetPhoto.builder()
//                  .photoSet(page.getPagePhotoSet())
//                  .hasNext(i != photosLength - 1)
//                  .hasPrevious(i != 0);
//                  .photo(photo)
//                  .createdAt(Instant.now())
//                  .build();
            savedPhotos.add(photo);
         }
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
