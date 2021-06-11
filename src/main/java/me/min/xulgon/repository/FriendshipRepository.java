package me.min.xulgon.repository;

import me.min.xulgon.model.Friendship;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
   @Query("SELECT f FROM Friendship f WHERE f.userA IN (:userA, :userB) AND f.userB IN (:userA, :userB)")
   Optional<Friendship> findByUsers(User userA, User userB);

   @Query("SELECT f FROM Friendship f WHERE f.userA = :user OR f.userB = :user")
   List<Friendship> findAllByUser(User user);

   @Transactional
   @Modifying
   @Query("DELETE FROM Friendship f WHERE f.userA IN (:userA, :userB) AND f.userB IN (:userA, :userB)")
   void deleteByUsers(User userA, User userB);
}
