package me.min.xulgon.repository;

import me.min.xulgon.model.Group;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
   Optional<GroupMember> findByUserAndGroup(User user, Group group);
   List<GroupMember> findAllByUser(User user);
   @Transactional
   @Modifying
   void deleteByUserAndGroup(User user, Group group);
}
