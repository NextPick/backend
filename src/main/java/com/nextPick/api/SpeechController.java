package com.nextPick.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class SpeechController {

    @Autowired
    private ClovaSpeechAPIService speechService;

    // 파일 업로드 및 변환용 엔드포인트
    @PostMapping("/convert-audio")
    public String convertAudio(@RequestParam("file") MultipartFile file) {
        try {
            // 파일을 서버에 임시로 저장 후, 해당 파일 경로를 사용하여 변환
            String filePath = file.getOriginalFilename(); // 임시로 파일 이름만 사용 (실제 파일 저장 경로 필요)
            file.transferTo(new java.io.File(filePath)); // 파일을 실제 경로에 저장

            return speechService.uploadAndConvertSpeechToText(filePath); // 파일 경로 전달
        } catch (IOException e) {
            return "Error occurred: " + e.getMessage();
        }
    }
}
