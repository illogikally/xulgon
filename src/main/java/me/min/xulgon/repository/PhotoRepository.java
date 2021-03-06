package me.min.xulgon.repository;

import me.min.xulgon.model.Content;
import me.min.xulgon.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
   List<Photo> findAllByParentContent(Content content);
}
