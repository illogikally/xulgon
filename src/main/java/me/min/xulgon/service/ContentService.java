package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.Content;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.ReactionRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContentService {

   private final ContentRepository contentRepository;
   private final ReactionRepository reactionRepository;

   public void deleteContent(Long id) {
      Content content = contentRepository.findById(id)
            .orElseThrow(RuntimeException::new);
      deleteContent(content);
   }
   public void deleteContent(Content content) {

      content.getComments()
//            .map(Content::getId)
            .forEach(this::deleteContent);

      content.getPhotos()
//            .map(Content::getId)
            .forEach(this::deleteContent);

      reactionRepository.deleteAllByContent(content);
      contentRepository.deleteById(content.getId());

   }

}
