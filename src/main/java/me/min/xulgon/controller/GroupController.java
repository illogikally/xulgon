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
   public ResponseEntity<Long> create(@RequestBody GroupRequest request) {
      return ResponseEntity.ok(groupService.create(request));
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
      return ResponseEntity.ok(groupService.getMembers(id));
   }

   @DeleteMapping("/{id}/quit")
   public ResponseEntity<Void> quit(@PathVariable Long id) {
      this.groupService.quit(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @PutMapping("/{id}/promote/{userId}")
   public ResponseEntity<Void> promote(@PathVariable Long id, @PathVariable Long userId) {
      groupService.promote(id, userId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @DeleteMapping("/{id}/kick/{userId}")
   public ResponseEntity<Void> kick(@PathVariable Long id, @PathVariable Long userId) {
      groupService.kick(id, userId);
      return new ResponseEntity<>(HttpStatus.OK);
   }



}

