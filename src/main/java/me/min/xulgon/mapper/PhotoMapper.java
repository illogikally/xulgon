package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.dto.ThumbnailDto;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.service.FollowService;
import me.min.xulgon.service.PrincipalService;
import org.springframework.core.env.Environment;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PhotoMapper {

   private final ContentRepository contentRepository;
   private final PrincipalService principalService;
   private final PageRepository pageRepository;
   private final UserMapper userMapper;
   private final ThumbnailMapper thumbnailMapper;
   private final Environment environment;
   private final FollowService followService;

   public Photo map(PhotoRequest photoRequest,
                    Pair<Integer, Integer> widthHeight,
                    String name,
                    PhotoSet set) {
      if (photoRequest == null || name == null) return null;

      return Photo.builder()
            .id(null)
            .body(photoRequest.getBody())
            .type(ContentType.PHOTO)
            .createdAt(Instant.now())
            .page(getPage(photoRequest))
            .user(principalService.getPrincipal())
            .reactions(List.of())
            .name(name)
            .photoSet(set)
            .reactionCount(0)
            .shareCount(0)
            .commentCount(0)
            .photos(List.of())
            .width(widthHeight.getFirst())
            .height(widthHeight.getSecond())
            .children(List.of())
            .privacy(photoRequest.getPrivacy())
            .parentContent(getParent(photoRequest.getParentId()))
            .build();
   }

   public PhotoResponse toPhotoResponse(Photo photo) {
      if (photo == null) return null;
      return PhotoResponse.builder()
            .id(photo.getId())
            .url(getUrl(photo))
            .userId(photo.getUser().getId())
            .thumbnails(getThumbnails(photo))
            .build();
   }

   private Map<ThumbnailType, ThumbnailDto> getThumbnails(Photo photo) {
      return photo.getThumbnails()
            .stream()
            .collect(Collectors.toMap(PhotoThumbnail::getType, thumbnailMapper::toDto));
   }

   public PhotoViewResponse toPhotoViewResponse(Photo photo) {
      if (photo == null) return null;

      return PhotoViewResponse.builder()
            .id(photo.getId())
            .type(photo.getType())
            .parentId(photo.getParentContent() == null ? null : photo.getParentContent().getId())
            .user(userMapper.toDto(photo.getUser()))
            .createdAt(toDate(photo.getCreatedAt()))
            .text(photo.getBody())
            .reactionCount(photo.getReactionCount())
            .commentCount(photo.getCommentCount())
            .isFollow(followService.isFollow(photo))
            .privacy(photo.getPrivacy())
            .shareCount(photo.getShareCount())
            .isReacted(isReacted(photo))
            .photos(List.of(toPhotoResponse(photo)))
            .build();

   }

   public PhotoViewResponse toPhotoViewSetResponse(PhotoSetPhoto photoSetPhoto,
                                                   Boolean hasNext,
                                                   Boolean hasPrevious) {
      if (photoSetPhoto == null) return null;

      Photo photo = photoSetPhoto.getPhoto();

      PhotoViewResponse response = toPhotoViewResponse(photo);
      response.setHasNext(hasNext);
      response.setHasPrevious(hasPrevious);
      return response;
   }

   public String getUrl(Photo photo) {
      if (photo == null) return null;
      String url = environment.getProperty("resource.url");
      return MessageFormat.format("{0}/{1}", url, photo.getName());
   }

   private Page getPage(PhotoRequest photoRequest) {
      Long pageId = photoRequest.getPageId() != null ? photoRequest.getPageId()
            : getParent(photoRequest.getParentId()).getPage().getId();

      return pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);
   }

   private String toDate(Instant instant) {
      var date = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(7));
      return DateTimeFormatter.ofPattern("d 'tháng' M 'lúc' HH:mm").format(date);
   }

   private Content getParent(Long parentId) {
      if (parentId == null) return null;
      return contentRepository.findById(parentId)
            .orElseThrow(ContentNotFoundException::new);
   }

   private boolean isReacted(Content content) {
      User principal = principalService.getPrincipal();
      return content.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(principal));
   }

}
