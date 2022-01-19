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
   List<Post> findAllByBodyContainsOrderByCreatedAtDesc(String body);

   @Query(nativeQuery = true,
         value = "select * from post p inner join content c on p.id = c.id " +
               "where page_id in (select group_id from group_member where user_id = :userId) " +
               "order by c.created_at desc limit :offset, :size")
   List<Post> getUserGroupFeed(Long userId, long size, long offset);

   @Query(nativeQuery = true,
         value = "select * from post p inner join content c on p.id = c.id " +
               "where page_id in (select page_id from follow f where f.user_id = :userId) " +
               "and c.user_id != :userId " +
               "order by c.created_at desc " +
               "limit :offset, :size")
   List<Post> getUserNewsFeed(Long userId, long size, long offset);
}
