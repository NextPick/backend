package com.nextPick.speechAPI;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

@Service
public class SpeechToTextService {

    @Autowired
    private SpeechSettings speechSettings;

    private final Logger logger = LoggerFactory.getLogger(SpeechToTextService.class);

    public String transcribe(MultipartFile audioFile, int frequency) throws IOException {
        if (audioFile.isEmpty()) {
            throw new IOException("Required part 'audioFile' is not present..");
        }

        // 오디오 파일을 byte array로 decode
        byte[] audioBytes = audioFile.getBytes();
        ByteString audioData = ByteString.copyFrom(audioBytes);

        // 설정 객체 생성
        RecognitionConfig recognitionConfig =
                RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.WEBM_OPUS) //파일은 FLAC 형식
                        .setSampleRateHertz(frequency) //아이폰 48000, 안드로이드 44100
                        .setLanguageCode("ko-kR") //영어 en-US, 한국어 ko-KR
                        .build();

        // 오디오 객체 생성
        RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
                .setContent(audioData)
                .build();

        // 클라이언트 인스턴스화
        try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {

            // 오디오-텍스트 변환 수행
            RecognizeResponse response = speechClient.recognize(recognitionConfig, recognitionAudio);
            StringBuilder transcript = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcript.append(alternative.getTranscript());
            }
            return transcript.toString();
        }
    }
}
