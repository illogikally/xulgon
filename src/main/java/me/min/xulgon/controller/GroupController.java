package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.GroupRequest;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.service.GroupService;
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
   public ResponseEntity<Void> create(@RequestBody GroupRequest request) {
      groupService.create(request);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }

   @GetMapping("/{id}")
   public ResponseEntity<GroupResponse> get(@PathVariable Long id) {
      return ResponseEntity.ok(groupService.get(id));
   }

   @PostMapping("/{id}/join-request")
   public ResponseEntity<Void> createJoinRequest(@PathVariable Long id) {
      groupService.createJoinRequest(id);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }


   @DeleteMapping("/{id}/join-request")
   public ResponseEntity<Void> deleteJoinRequest(@PathVariable Long id) {
      groupService.deleteJoinRequest(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }
//   @GetMapping("/{id}/posts")
//   public ResponseEntity<List<PostResponse>> getPosts(@PathVariable Long id) {
//
//   }
}

