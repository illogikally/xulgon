package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/photos")
@AllArgsConstructor
public class PhotoController {
   private final PhotoService photoService;

   @GetMapping("/{id}")
   public PhotoResponse get(@PathVariable Long id) {
      return photoService.get(id);
   }

   @DeleteMapping("{id}")
   public ResponseEntity<Void> delete(@PathVariable Long id) {
      photoService.delete(id);
      return new ResponseEntity<>(HttpStatus.OK);
   }
}
