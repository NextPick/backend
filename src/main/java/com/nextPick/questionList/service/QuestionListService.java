package com.nextPick.questionList.service;

import com.nextPick.api.openai.dto.ChatGPTRequest;
import com.nextPick.api.openai.dto.ChatGPTResponse;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.questionList.repository.QuestionListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionListService {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    private final QuestionListRepository questionListRepository;

    public boolean scoringInterview (long questionListId, String userResponse){
        QuestionList question = questionListRepository.findById(questionListId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        String prompt = "넌 이제 면접관이야 너는 문제와 해당 문제에 대한 대답을 가지고 있어.\n" +
                "사용자 문제에 대해서 대답을 하면 정답과 비교를 해서 정답인지 아닌지 판단해주고\n" +
                "그리고 100점만점에 몇점인지 채점도 해줘\n" +
                "예시는 아래와 같아\n" +
                "문제 : 안경에 대해 설명해주세요\n" +
                "정답 : 안경은 시력 저하가 있는 사람들을 위해 시력을 보정하는 도구로, 렌즈를 사용하여 시각적 정보를 강화하거나 왜곡을 교정하는 장치입니다.\n" +
                "사용자 대답 : 시력 저하된 사람들에게 시력을 보정해주는 도구\n" +
                "\n" +
                "판정 : 정답\n" +
                "채점 : 80점\n" +
                "\n" +
                "이제 아래의 문제에 대한 대답을 평가해줘\n" +
                "문제 : " + question.getQuestion() + "\n" +
                "정답 : " + question.getAnswer() + "\n" +
                "사용자 대답 : " + userResponse + "\n";
        ChatGPTRequest request = new ChatGPTRequest(model, prompt);
        ChatGPTResponse chatGPTResponse =  template.postForObject(apiURL, request, ChatGPTResponse.class);
        String GPTAnswer =  chatGPTResponse.getChoices().get(0).getMessage().getContent();
        return true;
    }
}
