package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Transactional
public class ContentService {

   private final ContentRepository contentRepository;
   private final PostRepository postRepository;
   private final CommentRepository commentRepository;
   private final PhotoRepository photoRepository;
   private final PrincipalService principalService;
   private final GroupRepository groupRepository;
   private final FriendshipRepository friendshipRepository;
   private final StorageService storageService;

   public void deleteContent(Long id) {
      Content content = contentRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);
      deleteContent(content);
      deletePostIfEmpty(content);
   }

   public void deleteContent(Content content) {
      content.getShares().forEach(post -> {
         post.setShare(null);
         postRepository.save(post);
      });

      var children = new ArrayList<>(content.getChildren());
      children.forEach(this::deleteContent);
      if (content.isNotType(ContentType.PHOTO)) {
         if (content.isType(ContentType.COMMENT)) {
            Content commentParent = content.getParentContent();
            decreaseCommentCount(commentParent);
         } else {
            Content sharedContent = ((Post) content).getShare();
            if (sharedContent != null) {
               decreaseShareCount(sharedContent);
            }
         }
         contentRepository.delete(content);
      } else {
         deletePhoto((Photo) content);
      }
   }

   private void decreaseCommentCount(Content content) {
      content.setCommentCount(content.getCommentCount() - 1);
      save(content);
   }

   private void decreaseShareCount(Content content) {
      content.setShareCount(content.getShareCount() - 1);
      save(content);
   }

   public void save(Content content) {
      switch (content.getType()) {
         case POST:
            postRepository.save((Post) content);
            break;
         case COMMENT:
            commentRepository.save((Comment) content);
            break;
         case PHOTO:
            photoRepository.save((Photo) content);
            break;
         default:
            break;
      }
   }

   private void deletePostIfEmpty(Content content) {
      if (content.isNotType(ContentType.POST)) return;
      boolean isTextEmpty = content.getBody() == null || content.getBody().isBlank();
      if (content.getPhotos().isEmpty() && isTextEmpty) {
         deleteContent(content.getId());
      }
   }

   public void deletePhoto(Photo photo) {
      photo.getThumbnails().stream()
            .map(PhotoThumbnail::getName)
            .forEach(storageService::delete);
      storageService.delete(photo.getName());
      photoRepository.deleteById(photo.getId());
   }

   public boolean isPrivacyAdequate(Content content) {
      return isPrivacyAdequate(content, null);
   }

   public boolean isPrivacyAdequate(Content content, User user) {
      Privacy privacy = getPrivacy(content, user);
      return content.getPrivacy().ordinal() <= privacy.ordinal();
   }

   public Content privacyFilter(Content content) {
      if (content == null) return null;
      return isPrivacyAdequate(content) ? content : null;
   }
   
   private Privacy getPrivacy(Content content, User user) {
      if (user == null) {
         user = principalService.getPrincipal();
      }
      User contentOwner = content.getUser();
      Page page = content.getPage();
      if (page.getType().equals(PageType.GROUP)) {
         Group group = groupRepository.findById(page.getId())
               .orElseThrow(PageNotFoundException::new);
         User finalUser = user;
         boolean isMember = group.getMembers()
               .stream()
               .anyMatch(member -> member.getUser().equals(finalUser));
         return isMember ? Privacy.GROUP : Privacy.PUBLIC;
      }

      return user.equals(contentOwner)
            ? Privacy.ME
            : friendshipRepository.findByUsers(user, contentOwner).isPresent()
            ? Privacy.FRIEND : Privacy.PUBLIC;
   }
}
