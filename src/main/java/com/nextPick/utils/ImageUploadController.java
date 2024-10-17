package com.nextPick.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/upload")
public class ImageUploadController {

    private final S3Uploader s3Uploader;

    @Autowired
    public ImageUploadController(S3Uploader s3Uploader) {
        this.s3Uploader = s3Uploader;
    }


        @PostMapping
        public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
            try {
                List<String> imageUrls = s3Uploader.uploadImages(files);  // 다중 파일 업로드 처리
                return ResponseEntity.ok(imageUrls);  // 업로드된 이미지들의 URL 리스트 반환
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload images");
            }
        }
    }

