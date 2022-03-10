package me.min.xulgon.repository;

import me.min.xulgon.model.Content;
import me.min.xulgon.model.NotificationSubject;
import me.min.xulgon.model.NotificationType;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationSubjectRepository extends JpaRepository<NotificationSubject, Long> {
   Optional<NotificationSubject> findByRecipientAndSubjectContentAndType(User recipient,
                                                                         Content content,
                                                                         NotificationType type);
   List<NotificationSubject> findAllByRecipientOrderByLatestCreatedAtDesc(User recipient);
}
