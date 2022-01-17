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

   @Query(nativeQuery = true,
         value = "select * from comment c inner join content co on c.id = co.id " +
               "where c.parent_content_id = :parentId " +
               "order by co.created_at limit :limit offset :offset"
   )
   List<Comment> getContentComments(Long parentId, Long limit, Long offset);

}
