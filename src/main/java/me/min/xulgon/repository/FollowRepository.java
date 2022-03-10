package me.min.xulgon.repository;

import me.min.xulgon.model.Content;
import me.min.xulgon.model.Follow;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
   @Transactional
   @Modifying
   void deleteByFollowerAndPage(User follower, Page page);
   @Transactional
   @Modifying
   void deleteByFollowerAndContent(User user, Content content);
   Optional<Follow> findByFollowerAndPage(User user, Page page);
   Optional<Follow> findByFollowerAndContent(User follower, Content content);
}
