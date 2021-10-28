package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.shaded.freemarker.template.utility.StringUtil;
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
@AllArgsConstructor
@Slf4j
public class StorageService {

   private final String DIR_PATH = "C://Storage/";

   public String store(MultipartFile photo) {
      String name = UUID.randomUUID().toString();
      String extension = StringUtils.getFilenameExtension(photo.getOriginalFilename());
//      String originalName = photo.getOriginalFilename() == null ? "" : photo.getOriginalFilename();
//      String extension = originalName.replaceAll(".+(?=\\.)", "");
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
      File file = new File(DIR_PATH + name);
      if (!file.delete()) {
         throw new RuntimeException("Can't delete file");
      }
   }
}