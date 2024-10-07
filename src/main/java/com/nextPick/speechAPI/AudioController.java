package com.nextPick.speechAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api")
public class AudioController {

    private final ClovaSpeechService clovaSpeechService;
    private final ObjectStorageUploader objectStorageUploader;

    @Value("${ncp.bucket.name}")
    private String bucketName;

    public AudioController(ClovaSpeechService clovaSpeechService, ObjectStorageUploader objectStorageUploader) {
        this.clovaSpeechService = clovaSpeechService;
        this.objectStorageUploader = objectStorageUploader;
    }

    // 프론트에서 음성 파일 전송 -> 백엔드에서 받아 처리
    @PostMapping("/upload-audio")
    public ResponseEntity<String> uploadAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            // 1. 파일이 제대로 전송되었는지 확인하기 위해 파일을 임시로 저장해봅니다.
            File tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + audioFile.getOriginalFilename());
            audioFile.transferTo(tempFile);  // 파일 저장

            System.out.println("파일 저장 성공: " + tempFile.getPath());

            // 2. Clova Speech API로 음성을 텍스트로 변환
            String convertedText = clovaSpeechService.convertSpeechToText(audioFile);

            // 3. 변환된 텍스트를 Object Storage에 저장
            String fileName = "converted-text-" + System.currentTimeMillis() + ".txt";
            objectStorageUploader.uploadTextToObjectStorage(fileName, convertedText);

            return ResponseEntity.ok("파일이 성공적으로 업로드되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("파일 업로드 실패: " + e.getMessage());
        }
    }

}
