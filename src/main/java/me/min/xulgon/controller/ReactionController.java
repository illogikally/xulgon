package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.ReactionDto;
import me.min.xulgon.service.ReactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reaction")
@AllArgsConstructor
public class ReactionController {
   private final ReactionService reactionService;

   @PostMapping
   public ResponseEntity<Void> react(@RequestBody ReactionDto reactionDto) {
      reactionService.react(reactionDto);
      return new ResponseEntity<>(HttpStatus.CREATED);
   }
}
