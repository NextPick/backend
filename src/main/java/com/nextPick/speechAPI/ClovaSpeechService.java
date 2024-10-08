package com.nextPick.speechAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class ClovaSpeechService {

    @Value("${clova.speech.api.url}")
    private String apiUrl;

    @Value("${clova.speech.api.key}")
    private String apiKey;

    // Clova Speech API로 음성 -> 텍스트 변환
    public String convertSpeechToText(MultipartFile audioFile) {
        try {
            // Clova Speech API 호출을 위한 URL 설정
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", apiKey);
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setDoOutput(true);

            // MultipartFile을 InputStream으로 변환하여 API 요청에 추가
            try (InputStream inputStream = audioFile.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    connection.getOutputStream().write(buffer, 0, bytesRead);
                }
            }

            // 응답 코드가 200이 아닐 경우 에러 메시지 출력
            if (connection.getResponseCode() != 200) {
                InputStream errorStream = connection.getErrorStream();
                try (Scanner errorScanner = new Scanner(errorStream)) {
                    StringBuilder errorResponse = new StringBuilder();
                    while (errorScanner.hasNext()) {
                        errorResponse.append(errorScanner.nextLine());
                    }
                    System.err.println("API 호출 실패: " + errorResponse.toString());
                }
            }

            // API 응답 받기
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                return response.toString();  // 변환된 텍스트 반환
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Clova Speech API 호출 실패: " + e.getMessage());
        }
    }
}