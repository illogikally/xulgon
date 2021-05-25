package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.mapper.PostMapper;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.Post;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PostRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class PostService {
   private final PostRepository postRepository;
   private final PageRepository pageRepository;
   private final UserRepository userRepository;
   private final PostMapper postMapper;

   @Transactional(readOnly = true)
   public List<PostResponse> getPostsByPage(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(() -> new RuntimeException("Page not found"));
      List<Post> posts = postRepository.findAllByPage(page);
      var date = LocalDateTime.ofInstant(posts.get(0).getCreatedAt(), ZoneOffset.ofHours(7));
      log.warn(DateTimeFormatter.ofPattern("dd 'tháng' MM 'lúc' hh:mm").format(date));
      return posts.stream()
            .map(postMapper::toDto)
            .collect(Collectors.toList());

   }
}
