package me.min.xulgon.repository;

import me.min.xulgon.model.ContentEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentEditHistoryRepository extends JpaRepository<ContentEditHistory, Long> {
}
