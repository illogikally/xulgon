package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.*;
import me.min.xulgon.model.GroupRole;
import me.min.xulgon.service.GroupService;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@AllArgsConstructor
public class GroupController {

   private final GroupService groupService;

   @PostMapping
   public ResponseEntity<Long> create(@RequestBody GroupRequest request) {
      return ResponseEntity.ok(groupService.create(request));
   }

   @GetMapping()
   public ResponseEntity<OffsetResponse<GroupResponse>> getGroups(OffsetRequest request) {
      return ResponseEntity.ok(groupService.getGroups(request));
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

   @GetMapping("{id}/role")
   public ResponseEntity<GroupRole> getRole(@PathVariable Long id) {
      return ResponseEntity.ok(groupService.getRole(id));
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

   @PutMapping("/{id}/demote/{userId}")
   public ResponseEntity<Void> demote(@PathVariable Long id, @PathVariable Long userId) {
      groupService.demote(id, userId);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @DeleteMapping("/{id}/kick/{userId}")
   public ResponseEntity<Void> kick(@PathVariable Long id, @PathVariable Long userId) {
      groupService.kick(id, userId);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}

