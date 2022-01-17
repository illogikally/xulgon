package me.min.xulgon.repository;

import me.min.xulgon.model.UserPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPageRepository extends JpaRepository<UserPage, Long> {
}
