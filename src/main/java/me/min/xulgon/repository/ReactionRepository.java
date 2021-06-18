package me.min.xulgon.repository;

import me.min.xulgon.model.Content;
import me.min.xulgon.model.Reaction;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
   Optional<Reaction> findTopByContentAndUserOrderByIdDesc(Content content, User user);

   @Transactional
   @Modifying
   void deleteAllByContent(Content content);
}
