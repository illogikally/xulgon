package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.*;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import me.min.xulgon.util.OffsetRequest;
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

   private final NotificationService notificationService;
   private final PostRepository postRepository;
   private final PostMapper postMapper;
   private final AuthenticationService authService;
   private final ProfileRepository profileRepository;
   private final PhotoService photoService;
   private final GroupRepository groupRepository;
   private final BlockService blockService;
   private final ContentService contentService;
   private final UserMapper userMapper;
   private final PageRepository pageRepository;
   private final FollowService followService;
   private final PhotoSetRepository photoSetRepository;
   private PhotoSetPhotoService photoSetPhotoService;
   private ContentRepository contentRepository;
   private final WebSocketService webSocketService;

   public OffsetResponse<PostResponse> getPostsByPage(Long id, OffsetRequest pageable) {
      Page page = pageRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      return page.getType() == PageType.GROUP
            ? getPostsByGroup(id, pageable) : getPostsByProfile(id, pageable);
   }

   @Transactional(readOnly = true)
   public OffsetResponse<PostResponse> getPostsByProfile(Long profileId, OffsetRequest pageable) {
      Profile profile = profileRepository.findById(profileId)
            .orElseThrow(PageNotFoundException::new);

      List<Post> posts = postRepository.getProfilePosts(
            profile.getId(),
            authService.getPrincipal().getId(),
            pageable.getPageSize() + 1,
            pageable.getAfter(),
            pageable.getBefore()
      );

      List<PostResponse> postResponses = posts
            .stream()
            .limit(pageable.getPageSize())
            .peek(post -> post.setPhotos(
                  post.getPhotos()
                        .stream()
                        .filter(contentService::isPrivacyAdequate)
                        .collect(Collectors.toList())))
            .map(postMapper::toDto)
            .collect(Collectors.toList());

      Boolean hasNext = posts.size() > pageable.getPageSize();
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
            .filter(contentService::isPrivacyAdequate)
            .filter(blockService::filter)
            .map(postMapper::toDto)
            .orElse(null);
   }

   public PostResponse save(PostRequest postRequest,
                            List<MultipartFile> photos,
                            List<PhotoRequest> photoRequests) {
      Page page = pageRepository.findById(postRequest.getPageId())
            .orElseThrow(PageNotFoundException::new);

      if (postRequest.getSharedContentId() != null) {
         Content sharedContent = contentRepository.findById(postRequest.getSharedContentId())
               .orElseThrow(ContentNotFoundException::new);
         sharedContent.setShareCount(sharedContent.getShareCount() + 1);
         contentRepository.save(sharedContent);
      }

      PhotoSet postPhotoSet = PhotoSet.generate(SetType.POST);
      postPhotoSet = photoSetRepository.save(postPhotoSet);
      Post savedPost = postRepository.save(postMapper.map(postRequest, postPhotoSet));

      List<Photo> savedPhotos = new ArrayList<>();
      photoRequests.forEach(request -> request.setParentId(savedPost.getId()));
      for (int i = 0; i < Math.min(photos.size(), photoRequests.size()); ++i) {
         Photo photo = photoService.save(photoRequests.get(i), photos.get(i));
         savedPhotos.add(photo);
      }

      followService.followContent(savedPost.getId());
      savedPost.setPhotos(savedPhotos);
      photoSetPhotoService.bulkInsertUnique(postPhotoSet, savedPhotos);
      photoSetPhotoService.bulkInsertUnique(page.getPagePhotoSet(), savedPhotos);
      sendNewPostNotification(savedPost);
      var postResponse = postMapper.toDto(savedPost);
      webSocketService.send(
            postRequest.getPageId(),
            WebSocketContentType.POST,
            postResponse,
            "/topic/post"
      );
      return postResponse;
   }

   private void sendNewPostNotification(Post post) {
      post.getPage().getFollows()
            .stream()
            .map(Follow::getFollower)
            .filter(user -> contentService.isPrivacyAdequate(post, user))
            .forEach(recipient ->
               notificationService.createNotification(
                     post.getUser(),
                     post,
                     post,
                     post,
                     post.getPage(),
                     recipient,
                     NotificationType.NEW_POST
               )
            );
   }

   public List<UserBasicDto> getCommenters(Long id) {
      Post post = postRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);
      return post.getAllComments().stream()
            .map(Comment::getUser)
            .distinct()
            .map(userMapper::toBasicDto)
            .collect(Collectors.toList());
   }
}
