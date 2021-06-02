package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import me.min.xulgon.dto.PhotoResponse;
import me.min.xulgon.service.PhotoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/photos")
@AllArgsConstructor
public class PhotoController {
   private final PhotoService photoService;

   @GetMapping("/{id}")
   public PhotoResponse get(@PathVariable Long id) {
      return photoService.get(id);
   }
}
