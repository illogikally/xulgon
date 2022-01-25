package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.shaded.freemarker.template.utility.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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

   public String store(MultipartFile photo) {
      String name = UUID.randomUUID().toString();
      String extension = StringUtils.getFilenameExtension(photo.getOriginalFilename());
      String fileName = name + "." + extension;
      Path path = Paths.get(DIR_PATH, fileName);
      try (OutputStream os = Files.newOutputStream(path)) {
         os.write(photo.getBytes());
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