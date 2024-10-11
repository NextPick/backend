//package com.nextPick.speechAPI;
//
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Map;
//
//@Service
//public class SpeechService {
//
//    private final RestTemplate restTemplate;
//
//    public SpeechService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public String processAudioFile(MultipartFile audioFile) throws Exception {
//        HttpHeaders headers = new HttpHeaders();
//        // API 키 설정
//        headers.set("X-NCP-APIGW-API-KEY-ID", "YOUR_CLIENT_ID");
//        headers.set("X-NCP-APIGW-API-KEY", "YOUR_CLIENT_SECRET");
//
//        // 요청 바디 구성
//        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        body.add("file", new ByteArrayResource(audioFile.getBytes()) {
//            @Override
//            public String getFilename() {
//                return audioFile.getOriginalFilename();
//            }
//        });
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        ResponseEntity<Map> response = restTemplate.exchange("YOUR_API_URL", HttpMethod.POST, requestEntity, Map.class);
//
//        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//            return (String) response.getBody().get("text"); // 변환된 텍스트 반환
//        } else {
//            throw new Exception("Failed to transcribe audio");
//        }
//    }
//}
