package me.min.xulgon.service;

import kotlin.Triple;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

@Service
@Transactional
@AllArgsConstructor
public class PhotoSetPhotoService {

   private final PhotoSetRepository photoSetRepository;
   private final PhotoRepository photoRepository;
   private final PhotoSetPhotoRepository photoSetPhotoRepository;
   private final PhotoMapper photoMapper;
   private final ContentService contentService;

   @Transactional(readOnly = true)
   public PhotoViewResponse get(Long setId, Long photoId) {
      PhotoSet set = photoSetRepository.findById(setId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);
      PhotoSetPhoto photoSetPhoto = photoSetPhotoRepository.findByPhotoSetAndPhoto(set, photo)
            .orElseThrow(RuntimeException::new);

      if (!contentService.privacyFilter(photo)) {
         return null;
      }

      return photoMapper.toPhotoViewSetResponse(
            photoSetPhoto,
            hasNext(set, photoSetPhoto.getId()),
            hasPrevious(set, photoSetPhoto.getId())
      );
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse getBefore(Long setId, Long photoId) {
      return getAdjacent(photoSetPhotoRepository::findBefore, setId, photoId);
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse getAfter(Long setId, Long photoId) {
      return getAdjacent(photoSetPhotoRepository::findAfter, setId, photoId);
   }

   private PhotoViewResponse getAdjacent(BiFunction<PhotoSet, Long, Optional<PhotoSetPhoto>> find,
                                         Long setId,
                                         Long photoId) {
      PhotoSet set = photoSetRepository.findById(setId)
            .orElseThrow(RuntimeException::new);
      Photo photo = photoRepository.findById(photoId)
            .orElseThrow(ContentNotFoundException::new);
      PhotoSetPhoto photoSetPhoto = photoSetPhotoRepository.findByPhotoSetAndPhoto(set, photo)
            .orElseThrow(RuntimeException::new);

      Optional<PhotoSetPhoto> optional = getAdjacent(find, set, photoSetPhoto.getId());

      if (optional.isPresent()) {
         Long photoSetPhotoId = optional.get().getId();
         boolean hasNext = hasNext(set, photoSetPhotoId);
         boolean hasPrevious = hasPrevious(set, photoSetPhotoId);
         return photoMapper.toPhotoViewSetResponse(optional.get(), hasNext, hasPrevious);
      }
      return null;
   }

   private Boolean hasNext(PhotoSet set, Long photoSetPhotoId) {
      return getAdjacent(photoSetPhotoRepository::findAfter, set, photoSetPhotoId).isPresent();
   }

   private Boolean hasPrevious(PhotoSet set, Long photoSetPhotoId) {
      return getAdjacent(photoSetPhotoRepository::findBefore, set, photoSetPhotoId).isPresent();
   }

   private Optional<PhotoSetPhoto> getAdjacent(BiFunction<PhotoSet, Long, Optional<PhotoSetPhoto>> find,
                                               PhotoSet set,
                                               Long photoSetPhotoId) {

      Optional<PhotoSetPhoto> optional;
      do {
         optional = find.apply(set, photoSetPhotoId);
         if (optional.isPresent()) {
            photoSetPhotoId = optional.get().getId();
         }
      } while (optional.isPresent() && !contentService.privacyFilter(optional.get().getPhoto()));

      return optional;
   }

   /**
    * Insert into PhotoSet_Photo table. If there is duplicate, delete it then
    * proceed to insert.
    * @param set the photo set to insert photo into.
    * @param photos the photos to insert.
    */
   public void bulkInsertUnique(PhotoSet set, List<Photo> photos) {
      List<PhotoSetPhoto> currentPhotoSetPhotos = photoSetPhotoRepository.findAllByPhotoSet(set);
      List<PhotoSetPhoto> toInsertPhotoSetPhotos = new ArrayList<>();

      photos.forEach(photo -> {
         toInsertPhotoSetPhotos.add(
               PhotoSetPhoto.builder()
                     .photoSet(set)
                     .photo(photo)
                     .createdAt(Instant.now())
                     .build()
         );
         var isDuplicate = currentPhotoSetPhotos
               .stream()
               .filter(item -> item.getPhoto().equals(photo))
               .findAny();
         isDuplicate.ifPresent(photoSetPhotoRepository::delete);
      });
      photoSetPhotoRepository.saveAll(toInsertPhotoSetPhotos);
   }
}
