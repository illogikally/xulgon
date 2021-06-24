package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.mapper.PhotoViewMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PhotoService {

   private final PhotoViewMapper photoViewMapper;
   private final PhotoRepository photoRepository;
   private final StorageService storageService;
   private final PageRepository pageRepository;
   private final PostRepository postRepository;
   private final PhotoMapper photoMapper;


   public Photo save(PhotoRequest photoRequest, MultipartFile photo) {
      return  photoRepository.save(photoViewMapper.map(photoRequest, storageService.store(photo)));
   }


   @Transactional(readOnly = true)
   public PhotoViewResponse get(Long id) {
      Photo photo = photoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Photo not found"));

      return photoViewMapper.toDto(photo);
   }

   @Transactional(readOnly = true)
   public List<PhotoResponse> getPhotosByPage(Long id) {
      Page page = pageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Page not found"));

      return postRepository.findAllByPageOrderByCreatedAtDesc(page, Pageable.unpaged())
            .stream()
            .map(Post::getPhotos)
            .flatMap(Collection::stream)
            .map(photoMapper::toDto)
            .collect(Collectors.toList());
   }

   public void delete(Long id) {
      Photo photo = photoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Photo not found"));
      String name = photo.getUrl().replaceAll(".*/", "");
      photoRepository.delete(photo);
      storageService.delete(name);
   }

}
