package me.min.xulgon.repository;

import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoSet;
import me.min.xulgon.model.PhotoSetPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoSetPhotoRepository extends JpaRepository<PhotoSetPhoto, Long> {
   Optional<PhotoSetPhoto> findByPhotoSetAndPhoto(PhotoSet set, Photo photo);
   List<PhotoSetPhoto> findAllByPhotoSetOrderById(PhotoSet set);
   Optional<PhotoSetPhoto> findTopByPhotoSetAndIdGreaterThanOrderById(PhotoSet set, Long id);
   Optional<PhotoSetPhoto> findTopByPhotoSetAndIdLessThanOrderByIdDesc(PhotoSet set, Long id);

   default Optional<PhotoSetPhoto> findItemAfterThisPhotoInSet(PhotoSet set, Long id) {
      return findTopByPhotoSetAndIdGreaterThanOrderById(set, id);
   }

   default Optional<PhotoSetPhoto> findItemBeforeThisInSet(PhotoSet set, Long id) {
      return findTopByPhotoSetAndIdLessThanOrderByIdDesc(set, id);
   }
}
