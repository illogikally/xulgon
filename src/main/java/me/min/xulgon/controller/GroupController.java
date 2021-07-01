package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.repository.GroupRepository;
import me.min.xulgon.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {

   private final GroupService groupService;
   private final GroupRepository groupRepository;

   @PostMapping
   public ResponseEntity<Void> create(@RequestBody GroupRequest request) {
      groupService.create(request);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }

   @GetMapping("/{id}")
   public ResponseEntity<GroupResponse> get(@PathVariable Long id) {
      return ResponseEntity.ok(groupService.get(id));
   }

   @PostMapping("/{id}/join-requests")
   public ResponseEntity<Void> createJoinRequest(@PathVariable Long id) {
      groupService.createJoinRequest(id);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }


   @DeleteMapping("/{id}/join-requests")
   public ResponseEntity<Void> deleteJoinRequest(@PathVariable Long id) {
      groupService.deleteJoinRequest(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @GetMapping("/{id}/join-requests")
   public ResponseEntity<List<GroupJoinRequestDto>> getJoinRequests(@PathVariable Long id) {
      return ResponseEntity.ok(groupService.getJoinRequests(id));
   }

   @GetMapping("/{id}/members")
   public ResponseEntity<List<GroupMemberDto>> getMembers(@PathVariable Long id) {
      var x =  groupRepository.findById(id)
            .orElseThrow(RuntimeException::new)
            .getMembers()
            .stream()
            .map(member -> GroupMemberDto.builder()
                  .avatarUrl(member.getUser().getProfile().getAvatar().getUrl())
                  .name(member.getUser().getFullName())
                  .role(member.getRole())
                  .build()
            )
            .collect(Collectors.toList());
      return ResponseEntity.ok(x);
   }

}

