package me.min.xulgon.service;

import com.sirv.SirvClientImpl;
import com.sirv.spring.RestTemplateAdapter;
import lombok.AllArgsConstructor;
import me.min.xulgon.dto.OffsetResponse;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import me.min.xulgon.util.OffsetRequest;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class PhotoService {

   private final PhotoMapper photoMapper;
   private final ContentService contentService;
   private final PhotoRepository photoRepository;
   private final PageRepository pageRepository;
   private final PhotoSetPhotoRepository photoSetPhotoRepository;
   private final PhotoThumbnailRepository thumbnailRepository;
   private PhotoSetRepository photoSetRepository;
   private PhotoSetPhotoService photoSetPhotoService;
   private FollowService followService;
   private final SirvService sirvService;


   public Photo save(PhotoRequest photoRequest, MultipartFile multipartFile) {
      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(multipartFile.getInputStream());
      }
      catch (IOException exception) {
         exception.printStackTrace();
         throw new RuntimeException("Error while reading MultipartFile to BufferedImage");
      }

      PhotoSet photoSet = photoSetRepository.save(PhotoSet.generate(SetType.PHOTO));
      Photo photo = photoMapper.map(
            photoRequest,
            Pair.of(bufferedImage.getWidth(), bufferedImage.getHeight()),
            upload(multipartFile),
            photoSet
      );
      photoSetPhotoService.bulkInsertUnique(photoSet, List.of(photo));
      Photo savedPhoto = photoRepository.save(photo);
      followService.followContent(savedPhoto.getId());
      return savedPhoto;
   }

   private String upload(MultipartFile file) {
      String name = UUID.randomUUID().toString();
      String extension = "jpg";
      String filename = name + "." + extension;
      sirvService.upload(filename, file);
      return filename;
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse get(Long id) {
      Photo photo = photoRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);

      return photoMapper.toPhotoViewResponse(photo);
   }

   @Transactional(readOnly = true)
   public OffsetResponse<PhotoResponse> getPhotosByPage(Long id, OffsetRequest pageable) {
      Page page = pageRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);

      var photos = photoSetPhotoRepository.findAllByPhotoSetOrderByIdDesc(
            page.getPagePhotoSet(),
            pageable.sizePlusOne()
      );
      boolean hasNext = photos.size() > pageable.getPageSize();
      var photoResponses = photos
            .stream()
            .map(PhotoSetPhoto::getPhoto)
            .filter(contentService::isPrivacyAdequate)
            .limit(pageable.getPageSize())
            .map(photoMapper::toPhotoResponse)
            .collect(Collectors.toList());

      return OffsetResponse
            .<PhotoResponse>builder()
            .data(photoResponses)
            .hasNext(hasNext)
            .build();
   }

   public void delete(Long id) {
      Photo photo = photoRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);
      contentService.deletePhoto(photo);
   }
}
