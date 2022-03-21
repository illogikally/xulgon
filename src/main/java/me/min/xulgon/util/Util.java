package me.min.xulgon.util;

import lombok.AllArgsConstructor;
import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoThumbnail;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

public class Util {
   private Util() {}

   public static String getPhotoUrl(Environment env, Photo photo) {
      if (photo == null) return null;
      String baseUrl = env.getProperty("app.resource-url");
      return baseUrl + "/" + photo.getName();
   }

   public static String getThumbnailUrl(Environment env, PhotoThumbnail photo) {
      if (photo == null) return null;
      String baseUrl = env.getProperty("app.resource-url");
      return baseUrl + "/" + photo.getName();
   }
}
