package me.min.xulgon.exception;

public class ContentNotFoundException extends RuntimeException {
   public ContentNotFoundException() {
      super("Content not found!");
   }
}
