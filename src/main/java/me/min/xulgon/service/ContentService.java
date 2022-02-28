package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContentService {

   private final ContentRepository contentRepository;
   private final ReactionRepository reactionRepository;
   private final PostRepository postRepository;
   private final CommentRepository commentRepository;
   private final PhotoRepository photoRepository;
   private final PrincipalService principalService;
   private final GroupRepository groupRepository;
   private final FriendshipRepository friendshipRepository;

   public void deleteContent(Long id) {
      Content content = contentRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);
      deleteContent(content);
   }

   public void deleteContent(Content content) {
      content.getComments()
            .forEach(this::deleteContent);

      content.getPhotos()
            .forEach(this::deleteContent);

      reactionRepository.deleteAllByContent(content);
      contentRepository.deleteById(content.getId());
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


   public boolean privacyFilter(Content content) {
      Privacy privacy = getPrivacy(content);
      switch (content.getType()) {
         case POST:
            return ((Post) content).getPrivacy().ordinal() <= privacy.ordinal();
         case PHOTO:
            return ((Photo) content).getPrivacy().ordinal() <= privacy.ordinal();
         default:
            return true;
      }
   }

   private Privacy getPrivacy(Content content) {
      User principal = principalService.getPrincipal();
      User contentOwner = content.getUser();
      Page page = content.getPage();
      if (page.getType().equals(PageType.GROUP)) {
         Group group = groupRepository.findById(page.getId())
               .orElseThrow(PageNotFoundException::new);
         boolean isMember = group.getMembers()
               .stream()
               .anyMatch(member -> member.getUser().equals(principal));
         return isMember ? Privacy.GROUP : Privacy.PUBLIC;
      }

      return principal.equals(contentOwner) ? Privacy.ME
            : friendshipRepository.findByUsers(principal, contentOwner).isPresent()
            ? Privacy.FRIEND : Privacy.PUBLIC;
   }
}
