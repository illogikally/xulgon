package me.min.xulgon.exception;

public class PageNotFoundException extends RuntimeException{
   public PageNotFoundException() {
      super("Page not found!");
   }
}
