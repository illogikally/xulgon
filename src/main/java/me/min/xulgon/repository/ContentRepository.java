package me.min.xulgon.repository;

import me.min.xulgon.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
   List<Content> findAllByParentContent(Content content);
}
