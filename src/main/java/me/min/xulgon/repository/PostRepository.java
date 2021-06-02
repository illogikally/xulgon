package me.min.xulgon.repository;

import me.min.xulgon.model.Page;
import me.min.xulgon.model.Post;
import me.min.xulgon.model.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
   List<Post> findAllByPageOrderByCreatedAtDesc(Page page);
}
