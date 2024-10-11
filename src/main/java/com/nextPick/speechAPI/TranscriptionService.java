//package com.nextPick.speechAPI;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//public class TranscriptionService {
//
//    private final TranscriptionRepository transcriptionRepository;
//    private final TranscriptionMapper transcriptionMapper;
//
//    @Value("${clova.speech.client-id}")
//    private String clientId;
//
//    @Value("${clova.speech.client-secret}")
//    private String clientSecret;
//
//    @Value("${clova.speech.api-url}")
//    private String apiUrl;
//
//    private final RestTemplate restTemplate;
//
//    @Autowired
//    public TranscriptionService(TranscriptionRepository transcriptionRepository,
//                                TranscriptionMapper transcriptionMapper,
//                                RestTemplate restTemplate) {
//        this.transcriptionRepository = transcriptionRepository;
//        this.transcriptionMapper = transcriptionMapper;
//        this.restTemplate = restTemplate;
//    }
//
//    // 클로바 API에 오디오 파일 전송
//    public String sendAudioToClova(MultipartFile audioFile) throws Exception {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
//            headers.set("X-NCP-APIGW-API-KEY", clientSecret);
//
//            // 요청 바디 구성
//            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//            body.add("file", new ByteArrayResource(audioFile.getBytes()) {
//                @Override
//                public String getFilename() {
//                    return audioFile.getOriginalFilename(); // 원래 파일 이름 설정
//                }
//            });
//
//            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//
//            // 클로바 API로 요청 보내기
//            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);
//
//            // 응답에서 텍스트 추출
//            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
//                Map<String, Object> responseBody = response.getBody();
//                return (String) responseBody.get("text");
//            } else {
//                throw new Exception("Failed to transcribe audio, status code: " + response.getStatusCode());
//            }
//        } catch (IOException e) {
//            throw new Exception("Error reading audio file: " + e.getMessage());
//        } catch (HttpClientErrorException e) {
//            throw new Exception("HTTP error: " + e.getStatusCode() + ", message: " + e.getResponseBodyAsString());
//        } catch (Exception e) {
//            throw new Exception("Error while sending audio to Clova API: " + e.getMessage());
//        }
//    }
//
//    // 변환된 텍스트를 DB에 저장
//    public void saveTranscription(TranscriptionDTO transcriptionDTO) throws Exception {
//        try {
//            Transcription transcription = transcriptionMapper.toEntity(transcriptionDTO);
//            transcriptionRepository.save(transcription);
//        } catch (Exception e) {
//            throw new Exception("Error while saving transcription: " + e.getMessage());
//        }
//    }
//}
