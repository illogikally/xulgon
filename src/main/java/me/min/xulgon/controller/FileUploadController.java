package me.min.xulgon.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.min.xulgon.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
@AllArgsConstructor
@Slf4j
public class FileUploadController {
    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<String> upload(@RequestPart("files") List<MultipartFile> files, @RequestPart("text") BigBoy xx) {
        log.warn("big boi " + xx);
        log.warn("aize " + files.size());
        return ResponseEntity.ok("heeeeeeeeeh");

//        return storageService.store(files.get(0));
    }

}
