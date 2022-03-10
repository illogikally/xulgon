package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.Content;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoThumbnail;
import me.min.xulgon.repository.CommentRepository;
import me.min.xulgon.repository.ContentRepository;
import me.min.xulgon.repository.PhotoRepository;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@AllArgsConstructor
public class StorageService {

   private Environment environment;

   public String store(BufferedImage bufferedImage) {
      String DIR_PATH = environment.getProperty("resource.path");
      Assert.notNull(DIR_PATH, "Image storage (resource.path) path is null.");
      String name = UUID.randomUUID().toString();
      String extension = "jpg";
      String fileName = name + "." + extension;
      try {
         Thumbnails.of(bufferedImage)
               .scale(1)
               .outputFormat("jpg")
               .toFile(new File(Path.of(DIR_PATH, fileName).toString()));
      } catch (IOException e) {
         e.printStackTrace();
      }
      return fileName;
   }

   public void delete(String name) {
      String DIR_PATH = environment.getProperty("resource.path");
      Assert.notNull(DIR_PATH, "Image storage (resource.path) path is null.");
      File file = new File(Paths.get(DIR_PATH, name).toString());
      System.out.println(file);
      System.out.println(file.exists());
      System.out.println("######################");
      if (!file.delete()) {
         throw new RuntimeException("Can't delete file");
      }
   }

}