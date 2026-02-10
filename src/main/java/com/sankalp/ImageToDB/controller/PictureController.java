package com.sankalp.ImageToDB.controller;

import com.sankalp.ImageToDB.entity.Pictures_1;
import com.sankalp.ImageToDB.repository.Pictures_1_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class PictureController {
    @Autowired
    private Pictures_1_Repository pictures1Repository;
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("name") String name,
                         @RequestParam("description") String description) throws IOException {
        Pictures_1 picture=new Pictures_1();
        picture.setName(name);
        picture.setDescription(description);
        picture.setImage(file.getBytes());
        pictures1Repository.save(picture);
        return "Uploaded";

    }
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id){
    Pictures_1 p=pictures1Repository.findById(id).orElseThrow();
    return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(p.getImage());
    }
}

//s3Client.putObject(
//        PutObjectRequest.builder()
//        .bucket(bucketName)
//        .key(imageName)
//        .contentType(image.getContentType())
//        .build(),
//    RequestBody.fromBytes(image.getBytes())
//        );
