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
import java.util.function.BiFunction;

@Service
@AllArgsConstructor
@Transactional
public class PhotoSetService {

   private final PhotoSetRepository photoSetRepository;
   private final PhotoRepository photoRepository;
   private final PhotoSetPhotoRepository photoSetPhotoRepository;
   private final PhotoMapper photoMapper;
   private final ContentService contentService;

   @Transactional(readOnly = true)
   public PhotoViewResponse getItem(Long setId, Long photoId) {
      PhotoSet set = photoSetRepository.findById(setId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);
      PhotoSetPhoto photoSetPhoto =
            photoSetPhotoRepository.findByPhotoSetAndPhoto(set, photo)
                  .orElse(null);
      if (!contentService.privacyFilter(photo)) {
         return null;
      }
      return photoMapper.toPhotoViewSetResponse(photoSetPhoto);
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse getItemBefore(Long setId, Long photoId) {
      return getItemAdjacent(
            photoSetPhotoRepository::findItemBeforeThisInSet,
            setId,
            photoId
      );
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse getItemAfter(Long setId, Long photoId) {
      return getItemAdjacent(
            photoSetPhotoRepository::findItemAfterThisPhotoInSet,
            setId,
            photoId
      );
   }

   private PhotoViewResponse getItemAdjacent(BiFunction<PhotoSet, Long, Optional<PhotoSetPhoto>> get,
                                             Long setId,
                                             Long photoId) {
      PhotoSet set = photoSetRepository.findById(setId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);
      var photoSetPhoto = photoSetPhotoRepository.findByPhotoSetAndPhoto(set, photo)
            .orElseThrow(RuntimeException::new);
      var photoSetPhotoAdj = get.apply(set, photoSetPhoto.getId())
            .orElseThrow(RuntimeException::new);
      if (!contentService.privacyFilter(photoSetPhotoAdj.getPhoto())) {
         return null;
      }
      return photoMapper.toPhotoViewSetResponse(photoSetPhotoAdj);
   }

   /**
    * Insert a new record to PhotoSet_Photo table. If there is duplicate, delete then
    * proceed to insert.
    * @param set the photo set to insert photo into.
    * @param photo the photo to insert.
    */
   public void insertUniqueToPhotoSet(PhotoSet set, Photo photo) {
      PhotoSetPhoto photoSetPhoto = photoSetPhotoRepository.findByPhotoSetAndPhoto(set, photo)
            .orElseThrow(RuntimeException::new);
      boolean hasPrevious
            = photoSetPhotoRepository.findItemBeforeThisInSet(set, photoSetPhoto.getId()).isPresent();
      var optional = set.getPhotoSetPhoto()
            .stream()
            .filter(item -> item.getPhoto().equals(photo))
            .findAny();
      optional.ifPresent(photoSetPhotoRepository::delete);
      photoSetPhotoRepository.save(
            PhotoSetPhoto.builder()
                  .photoSet(set)
                  .hasPrevious(hasPrevious)
                  .hasNext(false)
                  .photo(photo)
                  .createdAt(Instant.now())
                  .build()
      );
   }
}
