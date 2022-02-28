package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.repository.GroupRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchService {

   private final UserRepository userRepository;
   private final GroupRepository groupRepository;
   private final GroupMapper groupMapper;
   private final UserMapper userMapper;
   private final PostRepository postRepository;
   private final PostMapper postMapper;
   private ContentService contentService;


   public List<UserDto> searchByUsername(String name) {
      return userRepository.findAllByFullNameContains(name)
            .stream()
            .map(userMapper::toDto)
            .collect(Collectors.toList());
   }

   public List<PostResponse> searchByPost(String postBody) {
      return postRepository.findAllByBodyContainsOrderByCreatedAtDesc(postBody)
            .stream()
            .filter(contentService::privacyFilter)
            .map(postMapper::toDto)
            .collect(Collectors.toList());
   }

   public List<GroupResponse> searchByGroupName(String name) {
      return groupRepository.findAllByNameContains(name)
            .stream()
            .map(groupMapper::toDto)
            .collect(Collectors.toList());
   }
}
