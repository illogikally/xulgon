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


   @Query(value = "WITH latest AS (" +
         "  SELECT m.*, ROW_NUMBER() OVER (PARTITION BY conversation_id ORDER BY created_at DESC) AS rn" +
         "  FROM message AS m)" +
         " SELECT COUNT(*) FROM latest WHERE rn = 1 AND is_read = false AND receiver_id = :receiverId"
         , nativeQuery = true)
   Integer countUnread(Long receiverId);

   @Query(value = "WITH latest AS (" +
           "  SELECT m.*, ROW_NUMBER() OVER (PARTITION BY conversation_id ORDER BY created_at DESC) AS rn" +
           "  FROM message AS m)" +
           "SELECT * FROM latest WHERE rn = 1 AND (receiver_id = :userId OR sender_id = :userId);",
           nativeQuery = true)
   List<Message> getRecentConversations(Long userId);
}
