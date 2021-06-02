package me.min.xulgon.repository;

import me.min.xulgon.model.FriendRequest;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
   Optional<FriendRequest> findByRequestorAndRequestee(User requestor, User requestee);
   @Transactional
   @Modifying
   @Query("delete from FriendRequest f where f.requestor in (:requestor, :requestee) and f.requestee in (:requestor, :requestee)")
   void deleteByUsers(User requestor, User requestee);
   List<FriendRequest> findAllByRequestee(User requestee);
}
