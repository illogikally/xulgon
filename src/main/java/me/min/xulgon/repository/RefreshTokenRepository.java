package me.min.xulgon.repository;

import me.min.xulgon.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
   void deleteByToken(String token);
   Optional<RefreshToken> findByToken(String token);
}
