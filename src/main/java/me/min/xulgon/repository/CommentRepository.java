package me.min.xulgon.repository;

import me.min.xulgon.model.Comment;
import me.min.xulgon.model.Content;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
   List<Comment> findAllByParentContent(Content parent, Pageable pageable);
}
