package me.min.xulgon.repository;

import me.min.xulgon.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
   List<Group> findAllByNameContains(String name);
}
