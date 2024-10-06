package com.nextPick.api;

package com.nextPick.api;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
public class ObjectStorageService {

    private final AmazonS3 s3Client;

    public ObjectStorageService(
            @Value("${ncp.endpoint}") String endPoint,
            @Value("${ncp.region}") String region,
            @Value("${ncp.access.key}") String accessKey,
            @Value("${ncp.secret.key}") String secretKey) {

        this.s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }

    public void uploadTextToObjectStorage(String bucketName, String fileName, String convertedText) {
        try {
            byte[] contentBytes = convertedText.getBytes(StandardCharsets.UTF_8);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentBytes.length);
            metadata.setContentType("text/plain; charset=UTF-8");

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName,
                    new ByteArrayInputStream(contentBytes), metadata);

            s3Client.putObject(putObjectRequest);
            System.out.println("파일 업로드 완료: " + fileName);
        } catch (Exception e) {
            System.err.println("파일 업로드 실패: " + e.getMessage());
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }
}