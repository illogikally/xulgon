package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.mapper.MappingUtil;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GroupService {

   private final UserRepository userRepository;

   private final FollowRepository followRepository;
   private final GroupMapper groupMapper;
   private final GroupRepository groupRepository;
   private final AuthenticationService authService;
   private final GroupMemberRepository groupMemberRepository;
   private final UserMapper userMapper;
   private final GroupJoinRequestRepository groupJoinRequestRepository;

   public Long create(GroupRequest groupRequest) {
      Group group = groupMapper.map(groupRequest);
      group = groupRepository.save(group);
      GroupMember member = GroupMember.builder()
            .group(group)
            .createdAt(Instant.now())
            .user(authService.getLoggedInUser())
            .role(GroupRole.ADMIN)
            .build();

      groupMemberRepository.save(member);
      followRepository.save(Follow.builder()
            .user(authService.getLoggedInUser())
            .page(group)
            .createdAt(Instant.now())
            .build());

      return group.getId();
   }

   public List<GroupMemberDto> getMembers(Long id) {
      return groupRepository.findById(id)
            .orElseThrow(RuntimeException::new)
            .getMembers()
            .stream()
            .map(member -> GroupMemberDto.builder()
                  .user(userMapper.toDto(member.getUser()))
                  .avatarUrl(member.getUser().getUserPage().getAvatar().getUrl())
                  .name(member.getUser().getFullName())
                  .role(member.getRole())
                  .build()
            )
            .collect(Collectors.toList());
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

   public void promote(Long groupId, Long userId) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      User user = userRepository.findById(userId)
            .orElseThrow(RuntimeException::new);

      groupMemberRepository.findByUserAndGroup(user, group)
            .stream()
            .peek(member -> member.setRole(GroupRole.ADMIN))
            .findAny()
            .ifPresent(groupMemberRepository::save);
   }

   public void kick(Long groupId, Long userId) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      User user = userRepository.findById(userId)
            .orElseThrow(RuntimeException::new);

      groupMemberRepository.deleteByUserAndGroup(user, group);
      followRepository.deleteByUserAndPage(user, group);

   }

   public List<GroupJoinRequestDto> getJoinRequests(Long groupId) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      return group.getJoinRequests()
            .stream()
            .map(request -> GroupJoinRequestDto.builder()
                  .id(request.getId())
                  .user(userMapper.toDto(request.getUser()))
                  .createdAgo(MappingUtil.getCreatedAgo(request.getCreatedAt()))
                  .build())
            .collect(Collectors.toList());
   }

   public void quit(Long groupId) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      User user = authService.getLoggedInUser();
      groupMemberRepository.deleteByUserAndGroup(user, group);
      followRepository.deleteByUserAndPage(user, group);
   }
}