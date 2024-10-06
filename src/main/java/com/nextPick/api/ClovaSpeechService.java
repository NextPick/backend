package com.nextPick.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

@Service
public class ClovaSpeechService {

    @Value("${clova.speech.api.url}")
    private String clovaSpeechApiUrl;

    @Value("${clova.speech.api.key}")
    private String clovaSpeechApiKey;

    public HttpURLConnection convertSpeechToText(File audioFile) throws IOException {
        ClovaSpeechRequest clovaRequest = new ClovaSpeechRequest();
        return clovaRequest.sendRequest(audioFile, clovaSpeechApiUrl, clovaSpeechApiKey);
    }
}