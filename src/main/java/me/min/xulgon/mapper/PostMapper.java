package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PostRequest;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.FollowRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.service.AuthenticationService;
import me.min.xulgon.service.ContentService;
import me.min.xulgon.service.FollowService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostMapper {

   private final PhotoMapper photoMapper;
   private final AuthenticationService authenticationService;
   private final PageRepository pageRepository;
   private final UserMapper userMapper;
   private ContentMapper contentMapper;
   private FollowRepository followRepository;
   private FollowService followService;
   private ContentService contentService;
   private ContentRepository contentRepository;

   public Post map(PostRequest postRequest, PhotoSet set) {
      if (postRequest == null) return null;

      boolean hasShare = postRequest.getSharedContentId() != null;
      return Post.builder()
            .type(ContentType.POST)
            .createdAt(Instant.now())
            .commentCount(0)
            .reactionCount(0)
            .shareCount(0)
            .body(postRequest.getBody())
            .children(List.of())
            .photoSet(set)
            .reactions(List.of())
            .photos(List.of())
            .hasShare(hasShare)
            .share(getShare(postRequest))
            .privacy(postRequest.getPrivacy())
            .page(getPage(postRequest.getPageId()))
            .user(authenticationService.getPrincipal())
            .build();
   }

   public PostResponse toDto(Post post) {
      if (post == null) return null;

      return PostResponse.builder()
            .id(post.getId())
            .type(post.getType())
            .hasShare(post.getHasShare())
            .pageName(post.getPage().getName())
            .pageId(post.getPage().getId())
            .pageType(post.getPage().getType())
            .reactionCount(post.getReactionCount())
            .commentCount(post.getCommentCount())
            .user(userMapper.toDto(post.getUser()))
            .text(post.getBody())
            .isFollowPage(followService.isFollow(post.getPage()))
            .isFollow(isFollow(post))
            .photoSetId(post.getPhotoSet().getId())
            .shareCount(post.getShareCount())
            .privacy(post.getPrivacy())
            .sharedContent(contentMapper.toDto(contentService.privacyFilter(post.getShare())))
            .createdAt(MappingUtil.getCreatedAt(post.getCreatedAt()))
            .photoCount(post.getPhotos().size())
            .isReacted(isReacted(post))
            .photos(getPhotoResponses(post.getPhotos()))
            .build();
   }

   private Boolean isFollow(Content content) {
      User principal = authenticationService.getPrincipal();
      return followRepository.findByFollowerAndContent(principal, content).isPresent();
   }

   private Content getShare(PostRequest postRequest) {
      if (postRequest.getSharedContentId() == null) return null;
     return contentRepository.findById(postRequest.getSharedContentId())
            .orElseThrow(ContentNotFoundException::new);
   }

   private List<PhotoResponse> getPhotoResponses(List<Photo> photos) {
      return photos.stream()
            .limit(4)
            .map(photoMapper::toPhotoResponse)
            .collect(Collectors.toList());
   }


   private boolean isReacted(Content content) {
      User user = authenticationService.getPrincipal();

      return content.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

   private Page getPage(Long pageId) {
      return pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);
   }
}
