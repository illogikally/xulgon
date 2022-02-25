package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.model.Comment;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.Post;
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

}
