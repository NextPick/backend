//package com.nextPick.speechAPI;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.net.URI;
//import java.util.logging.Logger;
//
//@Service
//public class ObjectStorageUploader {
//
//    @Value("${ncp.endpoint}")
//    private String endpoint;
//
//    @Value("${ncp.region}")
//    private String region;
//
//    @Value("${ncp.access.key}")
//    private String accessKey;
//
//    @Value("${ncp.secret.key}")
//    private String secretKey;
//
//    @Value("${ncp.bucket.name}")
//    private String bucketName;
//
//    private static final Logger logger = Logger.getLogger(ObjectStorageUploader.class.getName());
//    private S3Client s3Client;
//
//    // S3 클라이언트 초기화
//    public void initializeClient() {
//        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
//        this.s3Client = S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
//                .endpointOverride(URI.create(endpoint))
//                .build();
//    }
//
//    public void uploadTextToObjectStorage(String fileName, String convertedText) throws IOException {
//        initializeClient();
//
//        // 임시 파일에 텍스트 저장
//        File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
//        try (FileWriter writer = new FileWriter(tempFile)) {
//            writer.write(convertedText);
//        }
//
//        // Object Storage에 파일 업로드
//        try {
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .build();
//            s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFile));
//            logger.info("파일 업로드 완료: " + fileName);
//        } catch (Exception e) {
//            throw new IOException("파일 업로드 중 오류 발생: " + e.getMessage());
//        }
//    }
//}
