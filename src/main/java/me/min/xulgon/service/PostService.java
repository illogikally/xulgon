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
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
   private final PhotoService photoService;
   private final GroupRepository groupRepository;
   private final BlockService blockService;
   private final ContentService contentService;
   private final PageRepository pageRepository;
   private final PhotoSetRepository photoSetRepository;
   private PhotoSetPhotoService photoSetPhotoService;

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

      List<PostResponse> postResponses = posts
            .stream()
            .limit(pageable.getPageSize())
            .peek(post -> post.setPhotos(
                  post.getPhotos()
                        .stream()
                        .filter(contentService::privacyFilter)
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
            .filter(contentService::privacyFilter)
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
      photoRequests.forEach(request -> request.setParentId(savedPost.getId()));
      for (int i = 0; i < Math.min(photos.size(), photoRequests.size()); ++i) {
         Photo photo = photoService.save(photoRequests.get(i), photos.get(i));
         savedPhotos.add(photo);
      }

      savedPost.setPhotos(savedPhotos);
      photoSetPhotoService.bulkInsertUnique(postPhotoSet, savedPhotos);
      photoSetPhotoService.bulkInsertUnique(page.getPagePhotoSet(), savedPhotos);
      return postMapper.toDto(savedPost);
   }
}
