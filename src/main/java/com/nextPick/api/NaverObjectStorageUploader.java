package com.nextPick.api;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.file.Paths;

public class NaverObjectStorageUploader {

    private static final String BUCKET_NAME = "nextpick-clova-bucket"; // 네이버 클라우드 버킷 이름
    private static final String OBJECT_KEY = "uploaded-audio/interview.m4a"; // 저장될 파일 경로 및 이름
    private static final String FILE_PATH = "C:\\recoder"; // 로컬에서 업로드할 파일 경로

    public static void main(String[] args) {
        // 1. 네이버 클라우드 Object Storage 액세스 및 시크릿 키 설정
        String accessKey = "YOUR_ACCESS_KEY";
        String secretKey = "YOUR_SECRET_KEY";

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        // 2. S3 클라이언트 생성 (NCP Object Storage는 AWS S3와 호환됨)
        S3Client s3 = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of("kr-standard")) // 네이버 클라우드 리전 설정
                .build();

        try {
            // 3. 파일을 업로드하는 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(OBJECT_KEY)
                    .build();

            // 4. 파일 업로드
            s3.putObject(putObjectRequest, Paths.get(FILE_PATH));
            System.out.println("파일 업로드 성공: " + OBJECT_KEY);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        } finally {
            // 5. 클라이언트 종료
            s3.close();
        }
    }
}

