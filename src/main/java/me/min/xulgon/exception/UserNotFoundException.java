package me.min.xulgon.exception;

public class UserNotFoundException extends RuntimeException {
   public UserNotFoundException() {
      super("User not found!");
   }
}
