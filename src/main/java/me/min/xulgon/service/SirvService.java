package me.min.xulgon.service;

import com.sirv.SirvClientImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class SirvService {
   private SirvClientImpl sirvClient;

   public void upload(String filename, MultipartFile file) {
      try {
         InputStream inputStream = file.getInputStream();
         sirvClient.getFilesClient().upload('/' + filename, inputStream);
      }
      catch (IOException ignored) {
         throw new RuntimeException("Error while uploading image.");
      }
   }

   public void delete(String filename) {
      sirvClient.getFilesClient().delete("/" + filename);
   }
}
