package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoSet;
import me.min.xulgon.model.PhotoSetPhoto;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.PhotoSetPhotoRepository;
import me.min.xulgon.repository.PhotoSetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class PhotoSetService {

   private final PhotoSetRepository photoSetRepository;
   private final PhotoRepository photoRepository;
   private final PhotoSetPhotoRepository photoSetPhotoRepository;
   private final PhotoMapper photoMapper;

   @Transactional(readOnly = true)
   public PhotoViewResponse getItemById(Long setId, Long photoId) {
      PhotoSet set = photoSetRepository.findById(setId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);
      PhotoSetPhoto photoSetPhoto =
            photoSetPhotoRepository.findByPhotoSetAndPhoto(set, photo)
                  .orElse(null);
      return photoMapper.toPhotoViewSetResponse(photoSetPhoto);
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse getItemByIndex(Long setId, Integer index) {
      PhotoSet set = photoSetRepository.findById(setId)
            .orElseThrow(RuntimeException::new);
      PhotoSetPhoto photoSetPhoto =
            photoSetPhotoRepository.findByPhotoSetAndPhotoIndex(set, index);
      return photoMapper.toPhotoViewSetResponse(photoSetPhoto);
   }

   public void insertToPhotoSet(PhotoSet set, Photo photo) {
      int photoSetLastIndex = getLastIndexAndSetHasNextTrue(set);
      photoSetPhotoRepository.save(
            PhotoSetPhoto.builder()
                  .photoSet(set)
                  .photoIndex(photoSetLastIndex + 1)
                  .hasNext(false)
                  .photo(photo)
                  .createdAt(Instant.now())
                  .build()
      );
   }

   public void insertUniqueToPhotoSet(PhotoSet set, Photo photo) {
      boolean isPresent = set.getPhotoSetPhoto().stream()
            .anyMatch(psp -> psp.getPhoto().equals(photo));
      if (!isPresent) {
         insertToPhotoSet(set, photo);
      }
   }

   /**
    * Returns the last index of the photo set and mark its last item's hasNext to true.
    * If the set is empty, returns 0.
    * @param photoSet
    * The photo set to get last index of.
    *
    * @return last index of the photo set.
    */
   public Integer getLastIndexAndSetHasNextTrue(PhotoSet photoSet) {
      int lastIndex = 0;
      Optional<PhotoSetPhoto> photoSetPhotoOptional =
            photoSetPhotoRepository.findTopByPhotoSetOrderByPhotoIndexDesc(photoSet);
      if (photoSetPhotoOptional.isPresent()) {
         lastIndex = photoSetPhotoOptional.get().getPhotoIndex();
         var lastPhotoSetPhoto = photoSetPhotoOptional.get();
         lastPhotoSetPhoto.setHasNext(true);
         photoSetPhotoRepository.save(lastPhotoSetPhoto);
      }
      return lastIndex;
   }
}
