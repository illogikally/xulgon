package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.GroupResponse;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.dto.UserDto;
import me.min.xulgon.mapper.GroupMapper;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.mapper.UserMapper;
import me.min.xulgon.model.GroupMember;
import me.min.xulgon.model.Post;
import me.min.xulgon.repository.GroupRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserRepository;
import me.min.xulgon.util.OffsetRequest;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SearchService {

   private final ContentService contentService;
   private final UserRepository userRepository;
   private final GroupRepository groupRepository;
   private final UserMapper userMapper;
   private final GroupMapper groupMapper;
   private final PostMapper postMapper;
   private final PostRepository postRepository;


   public List<UserDto> searchByUsername(String query) {
      return userRepository.findAll()
            .stream()
            .filter(user -> filter(user.getFullName(), query))
            .map(userMapper::toDto)
            .collect(Collectors.toList());
   }

   public OffsetResponse<PostResponse> searchByPost(String query, OffsetRequest request) {
      var posts = postRepository.findAll()
            .stream()
            .filter(post -> filter(post.getBody(), query))
            .filter(contentService::isPrivacyAdequate)
            .skip(request.getOffset())
            .collect(Collectors.toList());

      boolean hasNext = posts.size() > request.getPageSize();
      var responses = posts
            .stream()
            .limit(request.getPageSize())
            .map(postMapper::toDto)
            .collect(Collectors.toList());

      return OffsetResponse
            .<PostResponse>builder()
            .data(responses)
            .hasNext(hasNext)
            .build();
   }

   private boolean filter(String text, String query) {
      String normalized = Normalizer
            .normalize(text, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .toLowerCase();
      return normalized.contains(query.toLowerCase());
   }

   public List<GroupResponse> searchByGroupName(String query) {
      return groupRepository.findAll()
            .stream()
            .filter(group -> filter(group.getName(), query))
            .map(groupMapper::toDto)
            .collect(Collectors.toList());
   }
}
