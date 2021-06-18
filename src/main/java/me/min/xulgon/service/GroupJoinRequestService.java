package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.GroupJoinRequest;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.GroupRole;
import me.min.xulgon.repository.GroupJoinRequestRepository;
import me.min.xulgon.repository.GroupMemberRepository;
import me.min.xulgon.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@AllArgsConstructor
@Transactional
public class GroupJoinRequestService {

   private final GroupJoinRequestRepository groupJoinRequestRepository;
   private final GroupRepository groupRepository;
   private final GroupMemberRepository groupMemberRepository;

   public void accept(Long id) {
      GroupJoinRequest request = groupJoinRequestRepository.findById(id)
            .orElseThrow(RuntimeException::new);

      groupMemberRepository.save(GroupMember.builder()
            .role(GroupRole.MEMBER)
            .user(request.getUser())
            .group(request.getGroup())
            .createdAt(Instant.now())
            .build());

      groupJoinRequestRepository.deleteById(id);
   }


   public void deleteRequest(Long id) {
      groupJoinRequestRepository.deleteById(id);
   }
}
