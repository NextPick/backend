package com.nextPick.questionList.service;

import com.nextPick.api.openai.dto.ChatGPTRequest;
import com.nextPick.api.openai.dto.ChatGPTResponse;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.questionCategory.repository.QuestionCategoryRepository;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.questionList.repository.QuestionListRepository;
import com.nextPick.solves.service.SolvesService;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionListService extends ExtractMemberAndVerify {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    private final MemberRepository memberRepository;
    private final QuestionListRepository questionListRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final SolvesService solvesService;

    public void createQuestionList(QuestionList questionList,long questionCategoryId) {
        QuestionCategory questionCategory = questionCategoryRepository.findById(questionCategoryId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_CATEGORY_NOT_FOUND));
        questionList.setQuestionCategory(questionCategory);
        questionListRepository.save(questionList);
    }

    public QuestionList updateQuestionList(QuestionList questionList,long questionListId) {
        QuestionList findQuestionList = questionListRepository.findById(questionListId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));

        Optional.ofNullable(questionList.getQuestion())
                .ifPresent(question -> findQuestionList.setQuestion(question));
        Optional.ofNullable(questionList.getAnswer())
                .ifPresent(answer -> findQuestionList.setAnswer(answer));

        return questionListRepository.save(findQuestionList);
    }

    public void deleteQuestionList(long questionListId){
        QuestionList findQuestionList = questionListRepository.findById(questionListId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        questionListRepository.delete(findQuestionList);
    }

    public QuestionList findQuestionList(long questionListId){
        return questionListRepository.findById(questionListId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
    }

    public Page<QuestionList> findQuestionLists(int size, int page, long questionCategoryId,
                                                String keyword, String sort) {
        Sort sortBy;
        Pageable pageable;
        switch (sort) {
            case "correct_percent_asc":
                sortBy = Sort.by("correctRate").ascending();
                break;
            case "correct_percent_desc":
                sortBy = Sort.by("correctRate").descending();
                break;
            case "recent":
                sortBy = Sort.by("questionListId").descending();
                break;
            default:
                throw new IllegalArgumentException("Invalid sort type: " + sort);
        }
//        QuestionCategory questionCategory = questionCategoryRepository.findById(questionCategoryId)
//                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_CATEGORY_NOT_FOUND));
        pageable = PageRequest.of(page, size, sortBy);
        return questionListRepository.findByManyFilter(questionCategoryId,keyword,pageable);
    }

    public List<QuestionList> findQuestionLists(int size, long questionCategoryId) {
        return questionListRepository.findRandomQuestionsByCategory(questionCategoryId,size);
    }

    public boolean scoringInterview (long questionListId, String userResponse) {
        Member member = extractMemberFromPrincipal(memberRepository);
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
        boolean result = checkAnswer(GPTAnswer);
        System.out.println(GPTAnswer);
        solvesService.createOrUpdateSolves(question,member,result,userResponse);
        return result;
    }

    public boolean checkAnswer(String text) {
        // 각 라인을 분리
        String[] lines = text.split("\n");

        // "판정 :" 라인을 찾고 "정답"이 있는지 확인
        for (String line : lines) {
            if (line.startsWith("판정 : ") && line.contains("정답")) {
                return true;
            }
        }

        return false;
    }
}
