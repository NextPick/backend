package com.nextPick.speechAPI;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

@RestController
public class NaverController {
    @PostMapping("fileUpload")
    public String fileUpload(@RequestParam("uploadFile") MultipartFile uploadFile, HttpServletRequest req) {
        System.out.println("NaverCloudController STT : " + new Date());
        String uploadPath = req.getServletContext().getRealPath("/upload");
        String fileName = uploadFile.getOriginalFilename();
        String filePath = uploadPath + "/" + fileName;

        try {
            BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            os.write(uploadFile.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }

        String resp = NaverCloud.stt(filePath);

        return resp;
    }
}
