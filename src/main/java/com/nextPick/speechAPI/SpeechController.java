//package com.nextPick.speechAPI;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/speech")
//public class SpeechController {
//
//    private final SpeechService speechService;
//
//    public SpeechController(SpeechService speechService) {
//        this.speechService = speechService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile audioFile) {
//        try {
//            String transcribedText = speechService.processAudioFile(audioFile);
//            Map<String, String> response = new HashMap<>();
//            response.put("transcription", transcribedText);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("error", e.getMessage());
//            return ResponseEntity.status(500).body(errorResponse);
//        }
//    }
//}