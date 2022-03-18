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
   value =
         "SELECT * " +
         "FROM   post p " +
         "       INNER JOIN content c " +
         "               ON p.id = c.id " +
         "WHERE  page_id IN (SELECT page_id " +
         "                   FROM   follow " +
         "                   WHERE  follower_id = :userId " +
         "                          AND (SELECT type " +
         "                               FROM   page " +
         "                               WHERE  page.id = page_id) = 'GROUP') " +
         "       AND c.user_id NOT IN (SELECT b.blockee_id " +
         "                             FROM   block b " +
         "                             WHERE  b.blocker_id = :userId) " +
         "ORDER  BY c.created_at DESC " +
         "LIMIT  :offset, :size ")
   List<Post> getUserGroupFeed(Long userId, long size, long offset);

   @Query(nativeQuery = true,
   value =
         "SELECT * " +
         "FROM   post p " +
         "       INNER JOIN content c " +
         "               ON p.id = c.id " +
         "WHERE  c.id < :after " +
         "       AND c.id > :before " +
         "       AND c.page_id = :profileId " +
         "       AND (c.user_id = :userId " +
         "              OR c.privacy = 'PUBLIC' " +
         "              OR (c.privacy = 'FRIEND' " +
         "                   AND EXISTS (SELECT * " +
         "                               FROM   friendship f " +
         "                               WHERE  :userId IN (f.usera_id, f.userb_id ) " +
         "                                      AND c.user_id IN (f.usera_id, " +
         "                                                       f.userb_id )))) " +
         "ORDER  BY c.created_at DESC " +
         "LIMIT :size")
   List<Post> getProfilePosts(Long profileId,
                              Long userId,
                              int size,
                              Long before,
                              Long after);

   @Query(nativeQuery = true,
         value =
         "SELECT * " +
         "FROM   post p " +
         "       INNER JOIN content c " +
         "               ON p.id = c.id " +
         "WHERE  c.page_id IN (SELECT page_id " +
         "                     FROM   follow f " +
         "                     WHERE  f.follower_id = :userId " +
         "                            AND (SELECT type " +
         "                                 FROM   page " +
         "                                 WHERE  page.id = page_id) != 'GROUP') " +
         "       AND ( c.user_id = :userId " +
         "              OR c.privacy = 'PUBLIC' " +
         "              OR ( c.privacy = 'FRIEND' " +
         "                   AND EXISTS (SELECT * " +
         "                               FROM   friendship f " +
         "                               WHERE  :userId IN ( f.usera_id, f.userb_id ) " +
         "                                      AND c.user_id IN ( f.usera_id, " +
         "                                                       f.userb_id )) ) ) " +
         "       AND c.user_id != :userId " +
         "ORDER  BY c.created_at DESC " +
         "LIMIT  :offset, :size ")
   List<Post> getUserNewsFeed(Long userId, int size, long offset);
}
