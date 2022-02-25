package me.min.xulgon.repository;

import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoSet;
import me.min.xulgon.model.PhotoSetPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhotoSetPhotoRepository extends JpaRepository<PhotoSetPhoto, Long> {
   Optional<PhotoSetPhoto> findTopByPhotoSetOrderByPhotoIndexDesc(PhotoSet set);
   Optional<PhotoSetPhoto> findByPhotoSetAndPhoto(PhotoSet set, Photo photo);
   PhotoSetPhoto findByPhotoSetAndPhotoIndex(PhotoSet set, Integer index);
   List<PhotoSetPhoto> findAllByPhotoSetOrderByPhotoIndex(PhotoSet set);
}
