package me.min.xulgon.repository;


import me.min.xulgon.model.Message;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

   @Query("SELECT m FROM Message m " +
         "WHERE m.sender IN (:userA, :userB) AND m.receiver IN (:userA, :userB)" +
         "ORDER BY m.createdAt DESC ")
   List<Message> findAllByUsers(User userA, User userB);
}
