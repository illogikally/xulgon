package me.min.xulgon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContentNotFoundException extends RuntimeException {
   public ContentNotFoundException() {
      super("Content not found!");
   }
}
