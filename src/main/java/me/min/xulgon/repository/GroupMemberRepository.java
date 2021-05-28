package me.min.xulgon.repository;

import me.min.xulgon.model.Group;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
   Optional<GroupMember> findByUserAndGroup(User user, Group group);
}
