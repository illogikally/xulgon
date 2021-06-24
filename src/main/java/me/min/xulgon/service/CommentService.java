package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.CommentRequest;
import me.min.xulgon.dto.CommentResponse;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.mapper.CommentMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.CommentRepository;
import me.min.xulgon.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
   private final BlockService blockService;
   private final StorageService storageService;
   private final PhotoService photoService;

   public CommentResponse save(CommentRequest commentRequest,
                               MultipartFile photoMultipart) {
      Comment comment = commentRepository.save(commentMapper.map(commentRequest));
      if (!photoMultipart.isEmpty()) {
         PhotoRequest photoRequest = new PhotoRequest();
         photoRequest.setParentId(comment.getId());
         photoRequest.setPrivacy(Privacy.PUBLIC);
         Photo photo = photoService.save(photoRequest, photoMultipart);
         comment.setPhotos(List.of(photo));
      }
      return commentMapper.toDto(comment);
   }

   public void deleteComment(Long id) {
      commentRepository.deleteById(id);
   }

   @Transactional(readOnly = true)
   public List<CommentResponse> getCommentsByContent(Long contentId) {
      Content content = contentRepository.findById(contentId)
            .orElseThrow(() -> new RuntimeException("Content not found"));
      return commentRepository.findAllByParent(content).stream()
            .filter(blockService::filter)
            .map(commentMapper::toDto)
            .collect(Collectors.toList());

   }
}
