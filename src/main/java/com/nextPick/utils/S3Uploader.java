package com.nextPick.utils;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
@Getter
@Slf4j
@RequiredArgsConstructor
@Service
public class S3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 파일 업로드 메서드
    public String upload(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        try {
            // 파일 크기 제한 (예: 5MB)
            if (multipartFile.getSize() > 5 * 1024 * 1024) {
                throw new BusinessLogicException(ExceptionCode.IMAGE_TOO_LARGE);
            }

            // 파일 형식 검증 (예: JPEG, PNG)
            String contentType = multipartFile.getContentType();
            if (!Objects.equals(contentType, "image/jpeg") &&
                    !Objects.equals(contentType, "image/png")) {
                throw new BusinessLogicException(ExceptionCode.INVALID_IMAGE_FORMAT);
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(contentType);

            amazonS3.putObject(bucket, originalFilename, multipartFile.getInputStream(), metadata);

            return amazonS3.getUrl(bucket, originalFilename).toString();
        } catch (AmazonServiceException e) {
            if (e.getStatusCode() == 403) {
                log.error("S3 upload failed: Access Denied - " + e.getMessage());
                throw new BusinessLogicException(ExceptionCode.S3_ACCESS_DENIED);
            } else if (e.getStatusCode() == 404) {
                log.error("S3 upload failed: Bucket Not Found - " + e.getMessage());
                throw new BusinessLogicException(ExceptionCode.S3_BUCKET_NOT_FOUND);
            } else {
                log.error("S3 upload failed with AmazonServiceException: " + e.getMessage());
                throw new BusinessLogicException(ExceptionCode.IMAGE_UPLOAD_FAILED);
            }
        } catch (SdkClientException e) {
            log.error("S3 upload failed with SdkClientException: " + e.getMessage());
            throw new BusinessLogicException(ExceptionCode.S3_CONNECTION_ERROR);
        } catch (IOException e) {
            log.error("S3 upload failed with IOException: " + e.getMessage());
            throw new BusinessLogicException(ExceptionCode.IMAGE_UPLOAD_FAILED);
        }
    }


    public List<String> uploadImages(List<MultipartFile> multipartFiles) throws IOException {
        List<String> imageUrls = new ArrayList<>();

        for (MultipartFile file : multipartFiles) {
            try {
                validateFile(file);
                // S3에 업로드
                String imageUrl = upload(file);
                imageUrls.add(imageUrl);

            } catch (IOException e) {
                System.err.println("파일 업로드 실패: " + file.getOriginalFilename());
                e.printStackTrace();
                throw new IOException("이미지 업로드 중 오류가 발생했습니다.", e);
            }
        }

        return imageUrls;
    }

    private void validateFile(MultipartFile file) throws IOException {
        // 파일 크기 제한: 5MB
        long maxFileSize = 5 * 1024 * 1024;
        if (file.getSize() > maxFileSize) {
            throw new IOException("파일 크기가 5MB를 초과했습니다: " + file.getOriginalFilename());
        }
        String contentType = file.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new IOException("허용되지 않는 파일 형식입니다: " + file.getOriginalFilename());
        }
    }

    public void delete(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }



    public void deleteImages(List<String> imageUrls) {
        for (String imageUrl : imageUrls) {
            try {
                System.out.println("삭제 요청: " + imageUrl);
                String fileName = extractFileNameFromUrl(imageUrl);
                System.out.println("파일 이름: " + fileName);
                amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
                System.out.println("삭제 완료: " + imageUrl);

            } catch (Exception e) {
                System.err.println("이미지 삭제 실패: " + imageUrl);
                e.printStackTrace();
            }
        }
    }

    private String extractFileNameFromUrl(String url) {
        String encodedFileName = url.substring(url.lastIndexOf("/") + 1); // 마지막 슬래시 이후가 파일명
        try {
            return URLDecoder.decode(encodedFileName, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("파일명을 추출할 수 없습니다: " + url);
        }
    }
}