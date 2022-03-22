package me.min.xulgon.repository;

import me.min.xulgon.model.Block;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
   Optional<Block> findByBlockerAndBlockee(User blocker, User blockee);
   void deleteByBlockerAndBlockee(User blocker, User blockee);
   List<Block> findAllByBlockee(User blockee);
   List<Block> findAllByBlocker(User blocker);
}
