package com.nextPick.api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

@RestController
@RequestMapping("/api")
public class AudioController {

    private final String secretKey = "b3988e1b6eb74f608911a55c8d74d63f"; // 네이버 클라우드에서 제공된 Secret Key
    private final String bucketName = "nextpick-clova-bucket"; // Object Storage의 버킷 이름

    @Autowired
    private ClovaSpeechService clovaSpeechService;

    @Autowired
    private ObjectStorageService objectStorageService;

    @PostMapping("/upload-audio")
    public String uploadAudio(@RequestParam("audio") MultipartFile audioFile) {
        File tempFile = null;
        try {
            // 1. 파일을 임시 저장소에 저장
            tempFile = File.createTempFile("audio", ".wav");
            audioFile.transferTo(tempFile);

            // 2. Clova Speech API로 음성 파일 전송 및 변환된 텍스트 받기
            String convertedText = clovaSpeechService.convertSpeechToText(tempFile);

            // 3. 변환된 텍스트를 Object Storage에 저장
            String fileName = "converted_text_" + System.currentTimeMillis() + ".txt";
            objectStorageService.uploadTextToObjectStorage(bucketName, fileName, convertedText);

            return "음성 변환 및 텍스트 저장 완료: " + fileName;
        } catch (Exception e) {
            return "처리 중 오류 발생: " + e.getMessage();
        } finally {
            // 임시 파일 삭제
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}

// 3. 변환된 텍스트를 Object Storage에 저장
        ObjectStorageUploader storageUploader = new ObjectStorageUploader();
        String fileName = "converted_text_" + System.currentTimeMillis() + ".txt"; // 파일 이름 설정
        storageUploader.uploadTextToObjectStorage(bucketName, fileName, convertedText);

        return "음성 변환 및 텍스트 저장 완료: " + fileName;
    }
}
