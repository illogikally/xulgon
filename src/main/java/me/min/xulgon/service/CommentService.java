package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.mapper.CommentMapper;
import me.min.xulgon.model.Comment;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.CommentRepository;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class CommentService {
   private final CommentRepository commentRepository;
   private final ContentRepository contentRepository;
   private final CommentMapper commentMapper;
   private final PageRepository pageRepository;
   private final AuthenticationService authenticationService;

   public CommentResponse save(CommentRequest commentRequest) {
      Content parent = contentRepository.findById(commentRequest.getParentId())
            .orElseThrow(() -> new RuntimeException("Content not found"));
      User user = authenticationService.getLoggedInUser();
      Page page = pageRepository.findById(parent.getPage().getId())
            .orElseThrow(() -> new RuntimeException("Page not found"));

      Comment comment = commentMapper.map(commentRequest, page, user, parent);
      return commentMapper.toDto(commentRepository.save(comment));
   }

   @Transactional(readOnly = true)
   public List<CommentResponse> getCommentsByContent(Long contentId) {
      Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
      return commentRepository.findAllByParent(content).stream()
            .map(commentMapper::toDto)
            .collect(Collectors.toList());

   }
}