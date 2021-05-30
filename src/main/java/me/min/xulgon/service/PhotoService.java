package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Page;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.User;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PageRepository;
import me.min.xulgon.repository.PhotoRepository;
import me.min.xulgon.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
@Slf4j
public class PhotoService {

   private final PhotoMapper photoMapper;
   private final PhotoRepository photoRepository;
   private final StorageService storageService;


   public Photo save(PhotoRequest photoRequest, MultipartFile photo) {
      return  photoRepository.save(photoMapper.map(photoRequest, storageService.store(photo)));
   }

}
