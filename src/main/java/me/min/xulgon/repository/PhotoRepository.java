package me.min.xulgon.repository;

import me.min.xulgon.model.Page;
import me.min.xulgon.model.Photo;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
   List<Photo> findAllByPage(Page page);
   List<Photo> findAllByPageOrderByCreatedAtDesc(Page page, Pageable pageable);
}
