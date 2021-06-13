package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.GroupRequest;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.GroupJoinRequestRepository;
import me.min.xulgon.repository.GroupMemberRepository;
import me.min.xulgon.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class GroupService {

   private final GroupMapper groupMapper;
   private final GroupRepository groupRepository;
   private final AuthenticationService authService;
   private final GroupMemberRepository groupMemberRepository;
   private final GroupJoinRequestRepository groupJoinRequestRepository;

   public void create(GroupRequest groupRequest) {
      Group group = groupRepository.save(groupMapper.map(groupRequest));

      GroupMember member = GroupMember.builder()
            .group(group)
            .createdAt(Instant.now())
            .user(authService.getLoggedInUser())
            .role(GroupRole.ADMIN)
            .build();

      groupMemberRepository.save(member);
   }

   public GroupResponse get(Long id) {
      Group group = groupRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Group not found"));
      return groupMapper.toDto(group);
   }

   public void createJoinRequest(Long groupId) {
      User user = authService.getLoggedInUser();
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);

      groupJoinRequestRepository.save(GroupJoinRequest
            .builder()
            .createdAt(Instant.now())
            .user(user)
            .group(group)
            .build());
   }

   public void deleteJoinRequest(Long groupId) {
      User user = authService.getLoggedInUser();
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      groupJoinRequestRepository.deleteByUserAndGroup(user, group);
   }
}
