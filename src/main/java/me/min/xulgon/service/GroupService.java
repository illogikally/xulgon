package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.mapper.MappingUtil;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.data.util.Pair;
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
   private final PhotoMapper photoMapper;
   private final UserMapper userMapper;
   private final GroupJoinRequestRepository groupJoinRequestRepository;
   private final PhotoSetRepository photoSetRepository;
   private NotificationService notificationService;

   public Long create(GroupRequest groupRequest) {
      Group group = groupMapper.map(groupRequest);
      PhotoSet pageSet = PhotoSet.generate(SetType.PAGE);
      PhotoSet coverSet = PhotoSet.generate(SetType.COVER_PHOTO);
      pageSet = photoSetRepository.save(pageSet);
      coverSet = photoSetRepository.save(coverSet);
      group.setPagePhotoSet(pageSet);
      group.setCoverPhotoSet(coverSet);
      group = groupRepository.save(group);
      GroupMember member = GroupMember.builder()
            .group(group)
            .createdAt(Instant.now())
            .user(authService.getPrincipal())
            .role(GroupRole.ADMIN)
            .build();

      groupMemberRepository.save(member);
      followRepository.save(Follow.builder()
            .follower(authService.getPrincipal())
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
                  .avatarUrl(photoMapper.getUrl(member.getUser().getProfile().getAvatar()))
                  .name(member.getUser().getFullName())
                  .role(member.getRole())
                  .build()
            )
            .collect(Collectors.toList());
   }

   public GroupRole getRole(Long id) {
      User principal = authService.getPrincipal();
      Group group = groupRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);
      return groupMemberRepository.findByUserAndGroup(principal, group)
            .map(GroupMember::getRole)
            .orElse(null);
   }

   public GroupResponse get(Long id) {
      Group group = groupRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);
      return groupMapper.toDto(group);
   }

   public void createJoinRequest(Long groupId) {
      User principal = authService.getPrincipal();
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);

      boolean isRequestExisted = groupJoinRequestRepository.findByUserAndGroup(principal, group)
                  .isPresent();
      if (isRequestExisted) return;

      groupJoinRequestRepository.save(GroupJoinRequest
            .builder()
            .createdAt(Instant.now())
            .user(principal)
            .group(group)
            .build());

      group.getMembers().stream()
            .filter(member -> member.getRole().equals(GroupRole.ADMIN))
            .map(GroupMember::getUser)
            .forEach(admin -> {
               notificationService.createNotification(
                     principal,
                     null,
                     null,
                     null,
                     group,
                     admin,
                     NotificationType.GROUP_JOIN_REQUEST
               );
            });
   }

   public void deleteJoinRequest(Long groupId) {
      User user = authService.getPrincipal();
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      groupJoinRequestRepository.deleteByUserAndGroup(user, group);
   }

   public void promote(Long groupId, Long userId) {
      Pair<User, Group> userGroupPair = findUserGroupAndAuthorizeAdmin(userId, groupId);
      User user = userGroupPair.getFirst();
      Group group = userGroupPair.getSecond();

      groupMemberRepository.findByUserAndGroup(user, group)
            .stream()
            .peek(member -> member.setRole(GroupRole.ADMIN))
            .findAny()
            .ifPresent(groupMemberRepository::save);
   }

   public void kick(Long groupId, Long userId) {
      Pair<User, Group> userGroupPair = findUserGroupAndAuthorizeAdmin(userId, groupId);
      User user = userGroupPair.getFirst();
      Group group = userGroupPair.getSecond();

      groupMemberRepository.deleteByUserAndGroup(user, group);
      followRepository.deleteByFollowerAndPage(user, group);

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
      User user = authService.getPrincipal();
      groupMemberRepository.deleteByUserAndGroup(user, group);
      followRepository.deleteByFollowerAndPage(user, group);
   }

   public void demote(Long id, Long userId) {
      Pair<User, Group> userGroupPair = findUserGroupAndAuthorizeAdmin(userId, id);
      User user = userGroupPair.getFirst();
      Group group = userGroupPair.getSecond();

      groupMemberRepository.findByUserAndGroup(user, group)
            .stream()
            .peek(member -> member.setRole(GroupRole.MEMBER))
            .findAny()
            .ifPresent(groupMemberRepository::save);
   }

   private Pair<User, Group> findUserGroupAndAuthorizeAdmin(Long userId, Long groupId) {
      Group group = groupRepository.findById(groupId)
            .orElseThrow(RuntimeException::new);
      User user = userRepository.findById(userId)
            .orElseThrow(RuntimeException::new);

      groupMemberRepository.findByUserAndGroup(authService.getPrincipal(), group)
            .filter(member -> member.getRole().equals(GroupRole.ADMIN))
            .orElseThrow(() -> new RuntimeException(""));
      return Pair.of(user, group);
   }

   public OffsetResponse<GroupResponse> getGroups(OffsetRequest request) {
      var groups = groupRepository.findAll(request.sizePlusOne());
      boolean hasNext = groups.getSize() > request.getPageSize();
      var groupResponses = groups.stream()
            .limit(request.getPageSize())
            .map(groupMapper::toDto)
            .collect(Collectors.toList());
      return OffsetResponse
            .<GroupResponse>builder()
            .hasNext(hasNext)
            .data(groupResponses)
            .build();
   }

   private boolean isNotMember(Group group) {
      User principal = authService.getPrincipal();
      return group.getMembers()
            .stream()
            .map(GroupMember::getUser)
            .noneMatch(member -> member.equals(principal));
   }
}