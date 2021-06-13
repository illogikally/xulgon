package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PostResponse;
import me.min.xulgon.repository.PageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PageService {

   private final PageRepository pageRepository;
   private final PostService postService;

//   public List<PostResponse> getPosts(Long id) {
////      postService.getPostsByProfile()
//   }
}
