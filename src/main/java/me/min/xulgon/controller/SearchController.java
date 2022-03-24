package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.service.SearchService;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
public class SearchController {

   private final SearchService searchService;

   @GetMapping("/people/{name}")
   public ResponseEntity<List<UserDto>> searchByUser(@PathVariable String name) {
      return ResponseEntity.ok(searchService.searchByUsername(name));
   }

   @GetMapping("/posts/{body}")
   public ResponseEntity<OffsetResponse<PostResponse>> searchByPostBody(@PathVariable String body,
                                                                        OffsetRequest request) {
      return ResponseEntity.ok(searchService.searchByPost(body, request));
   }

   @GetMapping("/groups/{name}")
   public ResponseEntity<List<GroupResponse>> searchByGroupName(@PathVariable String name) {
      return ResponseEntity.ok(searchService.searchByGroupName(name));
   }
}
