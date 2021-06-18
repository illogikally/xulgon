package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.service.GroupJoinRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-join-requests/")
@AllArgsConstructor
public class GroupJoinRequestController {

   private final GroupJoinRequestService groupJoinRequestService;
   @PutMapping("/{id}/accept")
   public ResponseEntity<Void> accept(@PathVariable Long id) {
      groupJoinRequestService.accept(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

   @DeleteMapping("/{id}")
   public ResponseEntity<Void> decline(@PathVariable Long id) {
      groupJoinRequestService.deleteRequest(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }

}
