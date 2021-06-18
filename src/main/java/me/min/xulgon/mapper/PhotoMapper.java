package me.min.xulgon.mapper;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

@Service
@AllArgsConstructor
public class PhotoMapper {

   private final AuthenticationService authenticationService;
   private final ContentRepository contentRepository;
   private final PageRepository pageRepository;
   private final UserMapper userMapper;

   public Photo map(PhotoRequest photoRequest, String name) {
      if (photoRequest == null || name == null) return null;

      return Photo.builder()
            .id(null)
            .body(photoRequest.getBody())
            .type(ContentType.PHOTO)
            .createdAt(Instant.now())
            .page(getPage(photoRequest))
            .user(authenticationService.getLoggedInUser())
            .reactions(new LinkedList<>())
            .photos(new LinkedList<>())
            .sizeRatio(photoRequest.getSizeRatio())
            .comments(new LinkedList<>())
            .privacy(photoRequest.getPrivacy())
            .parent(getParent(photoRequest.getParentId()))
            .url("http://localhost:8080/contents/" + name)
            .build();
   }

   public PhotoResponse toDto(Photo photo) {
      if (photo == null) return null;

      return PhotoResponse.builder()
            .id(photo.getId())
            .parentId(photo.getParent() == null ? null : photo.getParent().getId())
            .sizeRatio(photo.getSizeRatio())
            .user(userMapper.toDto(photo.getUser()))
            .createdAt(toDate(photo.getCreatedAt()))
            .body(photo.getBody())
            .reactionCount(photo.getReactions().size())
            .commentCount(photo.getComments().size())
            .privacy(photo.getPrivacy())
            .shareCount(0)
            .isReacted(isReacted(photo))
            .url(photo.getUrl())
            .build();

   }

   private Page getPage(PhotoRequest photoRequest) {
      Long pageId = photoRequest.getPageId() != null ? photoRequest.getPageId()
            : getParent(photoRequest.getParentId()).getPage().getId();

      return pageRepository.findById(pageId)
            .orElseThrow(() -> new RuntimeException("Page not found"));
   }

   private String getUsername(Content content) {
      return content.getUser().getLastName() + " " + content.getUser().getFirstName();
   }

   private String toDate(Instant instant) {
      var date = LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(7));
      return DateTimeFormatter.ofPattern("d 'tháng' M 'lúc' HH:mm").format(date);
   }

   private Content getParent(Long parentId) {
      if (parentId == null) return null;
      return contentRepository.findById(parentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
   }

   private boolean isReacted(Content content) {
      User user = authenticationService.getLoggedInUser();

      return content.getReactions().stream()
            .map(Reaction::getUser)
            .anyMatch(reactor -> reactor.equals(user));
   }

}
