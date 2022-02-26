package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoRequest;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.dto.PhotoViewResponse;
import me.min.xulgon.exception.ContentNotFoundException;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.mapper.PhotoMapper;
import me.min.xulgon.model.*;
import me.min.xulgon.repository.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
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
   private final StorageService storageService;
   private final PageRepository pageRepository;
   private final PhotoSetPhotoRepository photoSetPhotoRepository;
   private final PhotoThumbnailRepository thumbnailRepository;
   private final Environment environment;


   public Photo save(PhotoRequest photoRequest, MultipartFile multipartFile) {
      BufferedImage bufferedImage;
      try {
         bufferedImage = ImageIO.read(multipartFile.getInputStream());
      }
      catch (IOException exception) {
         exception.printStackTrace();
         throw new RuntimeException();
      }

      Photo persistedPhoto = photoRepository.save(
            photoMapper.map(
                  photoRequest,
                  Pair.of(bufferedImage.getWidth(), bufferedImage.getHeight()),
                  storageService.store(bufferedImage)
            )
      );
      var thumbnails = Arrays.stream(ThumbnailType.values())
            .map(type -> createThumbnail(persistedPhoto, type, bufferedImage))
            .collect(Collectors.toList());
      persistedPhoto.setThumbnails(thumbnails);
      return persistedPhoto;
   }

   private PhotoThumbnail createThumbnail(Photo photo,
                                          ThumbnailType thumbnailType,
                                          BufferedImage bufferedImage) {
      String resourcePath = environment.getProperty("resource.path");
      String type = thumbnailType.toString();
      int size = thumbnailType.getSize();
      String fileName = MessageFormat.format("{0}.{1}.jpg", UUID.randomUUID(), type);
      assert resourcePath != null;

      int width, height;
      try {
         int minSize = Math.min(bufferedImage.getWidth(), bufferedImage.getHeight());
         bufferedImage = Thumbnails.of(bufferedImage)
               .sourceRegion(Positions.CENTER, minSize, minSize)
               .size(size, size)
               .asBufferedImage();
         width = bufferedImage.getWidth();
         height = bufferedImage.getHeight();
         File outputFile = new File(Path.of(resourcePath, fileName).toString());
         Thumbnails.of(bufferedImage)
               .scale(1)
               .outputFormat("jpg")
               .toFile(outputFile);
      }
      catch (IOException exception) {
         exception.printStackTrace();
         throw new RuntimeException();
      }

      PhotoThumbnail thumbnail = PhotoThumbnail.builder()
            .originalPhoto(photo)
            .width(width)
            .height(height)
            .type(thumbnailType)
            .name(fileName)
            .build();
      return thumbnailRepository.save(thumbnail);
   }

   @Transactional(readOnly = true)
   public PhotoViewResponse get(Long id) {
      Photo photo = photoRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);

      return photoMapper.toPhotoViewResponse(photo);
   }

   @Transactional(readOnly = true)
   public List<PhotoResponse> getPhotosByPage(Long id) {
      Page page = pageRepository.findById(id)
            .orElseThrow(PageNotFoundException::new);

      return photoSetPhotoRepository.findAllByPhotoSetOrderById(page.getPagePhotoSet())
            .stream()
            .map(PhotoSetPhoto::getPhoto)
            .filter(contentService::privacyFilter)
            .map(photoMapper::toPhotoResponse)
            .collect(Collectors.toList());
   }

   public void delete(Long id) {
      Photo photo = photoRepository.findById(id)
            .orElseThrow(ContentNotFoundException::new);
      String name = photo.getName();
      photoRepository.delete(photo);
      storageService.delete(name);
   }

}
