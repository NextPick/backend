//package com.nextPick.speechAPI;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping
//@CrossOrigin(origins = "http://localhost:3000")
//public class TranscriptionController {
//    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
//    private final TranscriptionService transcriptionService;
//
//    public TranscriptionController(TranscriptionService transcriptionService) {
//        this.transcriptionService = transcriptionService;
//    }
//
//    @PostMapping("/proxy-clova")
//    public ResponseEntity<String> proxyClovaApi(@RequestParam("audio") MultipartFile audioFile) {
//        logger.debug("Received audio file: {}", audioFile.getOriginalFilename()); // 로그 추가
//        try {
//            String transcribedText = transcriptionService.sendAudioToClova(audioFile);
//            return ResponseEntity.ok(transcribedText);
//        } catch (Exception e) {
//            logger.error("Error processing Clova API request: {}", e.getMessage()); // 에러 로그
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing Clova API request: " + e.getMessage());
//        }
//    }
//}
