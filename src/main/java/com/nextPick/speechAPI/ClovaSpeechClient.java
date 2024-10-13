package com.nextPick.speechAPI;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class ClovaSpeechClient {

    // Clova Speech secret key
    private static final String SECRET = "b3988e1b6eb74f608911a55c8d74d63f";
    // Clova Speech invoke URL
    private static final String INVOKE_URL = "https://clovaspeech-gw.ncloud.com/external/v1/9151/c99942b8dacb1a13da10defdca3c90f6af7f2ae292c4e6fa000041b08ece7121";

    private CloseableHttpClient httpClient = HttpClients.createDefault();
    private Gson gson = new Gson();

    private static final Header[] HEADERS = new Header[] {
            new BasicHeader("Accept", "application/json"),
            new BasicHeader("X-CLOVASPEECH-API-KEY", SECRET),
    };

    @Getter
    @Setter
    public static class Boosting {
        private String words;
    }

    @Getter
    @Setter
    public static class Diarization {
        private Boolean enable = Boolean.FALSE;
        private Integer speakerCountMin;
        private Integer speakerCountMax;
    }

    @Getter
    @Setter
    public static class Sed {
        private Boolean enable = Boolean.FALSE;
    }

    @Getter
    @Setter
    public static class NestRequestEntity {
        private String language = "ko-KR";
        //completion optional, sync/async (응답 결과 반환 방식(sync/async) 설정, 필수 파라미터 아님)
        private String completion = "sync";
        //optional, used to receive the analyzed results (분석된 결과 조회 용도, 필수 파라미터 아님)
        private String callback;
        //optional, any data (임의의 Callback URL 값 입력, 필수 파라미터 아님)
        private Map<String, Object> userdata;
        private Boolean wordAlignment = Boolean.TRUE;
        private Boolean fullText = Boolean.TRUE;
        //boosting object array (키워드 부스팅 객체 배열)
        private List<Boosting> boostings;
        //comma separated words (쉼표 구분 키워드)
        private String forbiddens;
        private Diarization diarization;
        private Sed sed;
    }

    /**
     *
     * recognize media using a file (로컬 파일 업로드 후 음성 인식 요청)
     * @param file required, the media file (필수 파라미터, 로컬 파일)
     * @param nestRequestEntity optional (필수 파라미터가 아님)
     * @return string (문자열 반환)
     */
    public String upload(File file, NestRequestEntity nestRequestEntity) {
        HttpPost httpPost = new HttpPost(INVOKE_URL + "/recognizer/upload");
        httpPost.setHeaders(HEADERS);
        HttpEntity httpEntity = MultipartEntityBuilder.create()
                .addTextBody("params", gson.toJson(nestRequestEntity), ContentType.APPLICATION_JSON)
                .addBinaryBody("media", file, ContentType.MULTIPART_FORM_DATA, file.getName())
                .build();
        httpPost.setEntity(httpEntity);
        return execute(httpPost);
    }

    private String execute(HttpPost httpPost) {
        try (final CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            final HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}