package com.nextPick.speechAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@RestController
public class NaverController {
    @Autowired
    private SpeechToTextService sttService;

    @PostMapping("fileUpload")
    public String fileUpload(@RequestParam("uploadFile") MultipartFile uploadFile, HttpServletRequest req) throws IOException {
        String transcribe = sttService.transcribe(uploadFile, 48000);
//        return ResponseUtil.SUCCESS("변환에 성공하였습니다.", transcribe);
        return transcribe;
    }
}
