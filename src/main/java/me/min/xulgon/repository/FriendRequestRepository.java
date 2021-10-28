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

   Optional<FriendRequest> findByRequesterAndRequestee(User requester, User requestee);

   @Transactional
   @Modifying
   @Query("DELETE FROM FriendRequest f " +
         "WHERE f.requester IN (:requester, :requestee) " +
            "AND f.requestee IN (:requester, :requestee)")
   void deleteByUsers(User requester, User requestee);

   List<FriendRequest> findAllByRequestee(User requestee);
}
