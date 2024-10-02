package com.nextPick.api;

import okhttp3.*;
import java.io.File;
import java.io.IOException;

public class ClovaSpeechAPI {

    private static final String SPEECH_API_URL = "https://clovaspeech-gw.ncloud.com/external/v1/9151/c99942b8dacb1a13da10defdca3c90f6af7f2ae292c4e6fa000041b08ece7121"; // 요청 URL
    private static final String SPEECH_KEY = "6bfb5de29a3545348970c435e8835c28";
    private static final String BUCKET_KEY = "h61ufgwfb4";

    public static void main(String[] args) throws IOException {
        File audioFile = new File("C:\\recoder\\recode.wav");


        OkHttpClient client = new OkHttpClient();

        // RequestBody 생성 방식 수정
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.getName(),
                        RequestBody.create(MediaType.parse("audio/wav"), audioFile)) // 변경된 메서드 사용
                .addFormDataPart("language", "ko") // 한국어 설정
                .addFormDataPart("completion", "sync") // 동기 요청 (비동기는 async)
                .build();

        // POST 요청 생성
        Request request = new Request.Builder()
                .url(SPEECH_API_URL)
                .addHeader("X-NCP-APIGW-API-KEY", SPEECH_KEY)
                .addHeader("X-NCP-APIGW-API-KEY-ID", BUCKET_KEY)
                .post(body)
                .build();

        // 응답 처리
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            // 응답 출력
            System.out.println(response.body().string());
        }
    }
}
