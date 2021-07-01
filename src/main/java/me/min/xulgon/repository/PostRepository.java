package me.min.xulgon.repository;

import me.min.xulgon.model.Page;
import me.min.xulgon.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
   List<Post> findAllByPageOrderByCreatedAtDesc(Page page, Pageable pageable);
   @Query(nativeQuery = true,
   value = "select * from post p inner join content c on p.id = c.id " +
         "where c.page_id in (" +
         "select group_id from group_member where user_id = :userId" +
         ")  order by c.created_at desc limit :limit offset :offset")
   List<Post> getUserGroupFeed(Long userId, long limit, long offset);
}
