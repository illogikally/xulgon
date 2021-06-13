package me.min.xulgon.repository;

import me.min.xulgon.model.Group;
import me.min.xulgon.model.GroupJoinRequest;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {
  @Transactional
  @Modifying
  void deleteByUserAndGroup(User user, Group group);
}
