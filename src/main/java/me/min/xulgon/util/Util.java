package me.min.xulgon.util;

import me.min.xulgon.model.Photo;
import me.min.xulgon.model.PhotoThumbnail;
import org.springframework.core.env.Environment;
import org.springframework.data.util.Pair;

import java.awt.image.BufferedImage;
import java.util.*;

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

   public static String getDominantColorLeft(BufferedImage image) {
      return getHexColor(image, Pair.of(0F, .1F));
   }

   public static String getDominantColorRight(BufferedImage image) {
      return getHexColor(image, Pair.of(.9F, 1F));
   }

   public static String getHexColor(BufferedImage image, Pair<Float, Float> bound) {
      Map<Integer, Integer> colorMap = new HashMap<>();
      int height = image.getHeight();
      int width = image.getWidth();

      for (int i = (int) (width * bound.getFirst()); i < width * bound.getSecond(); i++) {
         for (int j = 0; j < height * .5; j++) {
            int rgb = image.getRGB(i, j);
            Integer counter = colorMap.get(rgb);
            if (counter == null) {
               counter = 0;
            }
            int amount = isGray(getRGBArr(rgb)) ? 1 : 2;
            colorMap.put(rgb, counter + amount);
         }
      }
      return getMostCommonColor(colorMap);
   }

   private static String getMostCommonColor(Map<Integer, Integer> map) {
      List<Map.Entry<Integer, Integer>> list = new LinkedList<>(map.entrySet());

      list.sort((Map.Entry<Integer, Integer> obj1, Map.Entry<Integer, Integer> obj2)
            -> ((Comparable) obj1.getValue()).compareTo(obj2.getValue()));

      Map.Entry<Integer, Integer> entry = list.get(Math.max(list.size() - 1, 0));
      int[] rgb = getRGBArr(entry.getKey());

      return "#" + Integer.toHexString(rgb[0])
            + Integer.toHexString(rgb[1])
            + Integer.toHexString(rgb[2]);
   }

   private static int[] getRGBArr(int pixel) {
      int alpha = (pixel >> 24) & 0xff;
      int red = (pixel >> 16) & 0xff;
      int green = (pixel >> 8) & 0xff;
      int blue = (pixel) & 0xff;

      return new int[]{red, green, blue};
   }

   private static boolean isGray(int[] rgbArr) {
      int rgDiff = rgbArr[0] - rgbArr[1];
      int rbDiff = rgbArr[0] - rgbArr[2];
      // Filter out black, white and grays...... (tolerance within 10 pixels)
      int tolerance = 10;
      if (rgDiff > tolerance || rgDiff < -tolerance) {
         return rbDiff <= tolerance && rbDiff >= -tolerance;
      }
      return true;
   }
}
