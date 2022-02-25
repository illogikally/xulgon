package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.mapstruct.ap.shaded.freemarker.template.utility.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

   @Value("${resource.path}")
   private String DIR_PATH;

   public String store(BufferedImage bufferedImage) {
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
      File file = new File(Paths.get(DIR_PATH, name).toString());
      if (!file.delete()) {
         throw new RuntimeException("Can't delete file");
      }
   }
}