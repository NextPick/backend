package com.nextPick.api;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ObjectStorageUploader {

    private final S3Client s3;

    public ObjectStorageUploader() {
        // AWS S3 클라이언트 생성 (네이버 클라우드 Object Storage와 호환)
        this.s3 = S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 해당하는 네이버 클라우드 지역 설정
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    // 변환된 텍스트를 파일로 저장 후 Object Storage에 업로드
    public void uploadTextToObjectStorage(String bucketName, String fileName, String convertedText) {
        try {
            // 텍스트 파일로 저장
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(convertedText);
            }

            // Object Storage에 파일 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3.putObject(putObjectRequest, tempFile.toPath());

            System.out.println("파일 업로드 완료: " + fileName);

        } catch (S3Exception | IOException e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
        }
    }
}
