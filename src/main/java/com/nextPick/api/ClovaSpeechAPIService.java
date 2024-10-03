package com.nextPick.api;

import okhttp3.*;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Service
public class ClovaSpeechAPIService {

    private static final String SPEECH_API_URL = "https://clovaspeech-gw.ncloud.com/external/v1/9151/c99942b8dacb1a13da10defdca3c90f6af7f2ae292c4e6fa000041b08ece7121";
    private static final String SPEECH_KEY = "6bfb5de29a3545348970c435e8835c28";
    private static final String BUCKET_KEY = "6rz4fbbtj8";
    private static final String BUCKET_NAME = "nextpick-clova-bucket";
    private static final String OBJECT_KEY = "uploaded-audio/interview.m4a";
    private static final String FILE_PATH = "C:\\recoder";

    public String uploadAndConvertSpeechToText(String filePath) throws IOException {
        // 1. 파일을 네이버 Object Storage에 업로드
        uploadFileToNaverStorage(filePath);

        // 2. 업로드한 파일을 사용하여 Clova Speech API로 음성을 텍스트로 변환
        return convertSpeechToText(filePath);
    }

    public void uploadFileToNaverStorage(String filePath) {
        String accessKey = "YOUR_ACCESS_KEY";
        String secretKey = "YOUR_SECRET_KEY";

        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        S3Client s3 = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(Region.of("kr-standard"))
                .build();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(OBJECT_KEY)
                    .build();

            s3.putObject(putObjectRequest, Paths.get(filePath));
            System.out.println("파일 업로드 성공: " + OBJECT_KEY);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        } finally {
            s3.close();
        }
    }

    public String convertSpeechToText(String filePath) throws IOException {
        File audioFile = new File(filePath);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(MediaType.parse("audio/wav"), audioFile))
                .addFormDataPart("language", "ko")
                .addFormDataPart("completion", "sync")
                .build();

        Request request = new Request.Builder()
                .url(SPEECH_API_URL)
                .addHeader("X-NCP-APIGW-API-KEY", SPEECH_KEY)
                .addHeader("X-NCP-APIGW-API-KEY-ID", BUCKET_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            return response.body().string();
        }
    }
}
