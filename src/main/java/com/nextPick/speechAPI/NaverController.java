package com.nextPick.speechAPI;

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
    @PostMapping("fileUpload")
    public String fileUpload(@RequestParam("uploadFile") MultipartFile uploadFile, HttpServletRequest req) throws IOException {
        System.out.println("NaverCloudController STT : " + new Date());
        String resp = "";
        try{
            File tempFile = File.createTempFile("temp", uploadFile.getOriginalFilename());
            uploadFile.transferTo(tempFile);
            tempFile.deleteOnExit(); // 애플리케이션 종료 시 임시 파일 자동 삭제

            final ClovaSpeechClient clovaSpeechClient = new ClovaSpeechClient();
            ClovaSpeechClient.NestRequestEntity requestEntity = new ClovaSpeechClient.NestRequestEntity();
            resp = clovaSpeechClient.upload(tempFile, requestEntity);



//            resp = NaverCloud.stt(tempFile);
        }catch (Exception e){
            System.out.println("MultipartFile trans file Fail");
        }


        return resp;
    }
}
