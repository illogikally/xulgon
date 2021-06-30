package me.min.xulgon.repository;

import me.min.xulgon.model.Notification;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
   List<Notification> findAllByRecipientOrderByCreatedAtDesc(User recipient);
}
