package com.carservice.carservicecmsbackend.controller;

import com.carservice.carservicecmsbackend.model.Photo;
import com.carservice.carservicecmsbackend.service.PhotoService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
public class PhotoController {
    private final PhotoService photoService;
    private static final String UPLOAD_DIR = "src/main/resources/images";

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @GetMapping
    public List<Photo> getAllPhotos() {
        return photoService.getAllPhotosSorted();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Photo> getPhotoById(@PathVariable Long id) {
        Photo photo = photoService.getPhotoById(id);
        if (photo != null) {
            return ResponseEntity.ok(photo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file, @RequestParam("orderIndex") Integer orderIndex) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());


            Photo photo = new Photo();
            photo.setOrderIndex(orderIndex);
            photo.setPath(filePath.toString());
            photoService.savePhoto(photo);

            return ResponseEntity.ok("Photo uploaded and saved: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error while uploading file.");
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadPhotoById(@PathVariable Long id) {
        try {
            Photo photo = photoService.getPhotoById(id);
            if (photo == null) {
                return ResponseEntity.notFound().build();
            }


            Path filePath = Paths.get(photo.getPath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhotoById(@PathVariable Long id) {
        if (photoService.getPhotoById(id) != null) {
            photoService.deletePhotoById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
