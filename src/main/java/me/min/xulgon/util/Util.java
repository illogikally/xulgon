package me.min.xulgon.util;

import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoThumbnail;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collector;

public class Util {
   private Util() {}

   public static String getPhotoUrl(Environment env, Photo photo) {
      if (photo == null) return null;
      String baseUrl = env.getProperty("resource.url");
      return baseUrl + "/" + photo.getName();
   }

   public static String getThumbnailUrl(Environment env, PhotoThumbnail photo) {
      if (photo == null) return null;
      String baseUrl = env.getProperty("resource.url");
      return baseUrl + "/" + photo.getName();
   }
}
