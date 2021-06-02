package me.min.xulgon.repository;

import me.min.xulgon.model.Friendship;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
   @Query("SELECT f from Friendship f WHERE f.userA in (:userA, :userB) and f.userB in (:userA, :userB)")
   Optional<Friendship> findByUsers(User userA, User userB);

   @Query("select f from Friendship f where f.userA = :user or f.userB = :user")
   List<Friendship> findAllByUser(User user);
}
