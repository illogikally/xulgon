package me.min.xulgon.repository;

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
   void deleteByUserAndPage(User user, Page page);
   Optional<Follow> findByUserAndPage(User user, Page page);
}
