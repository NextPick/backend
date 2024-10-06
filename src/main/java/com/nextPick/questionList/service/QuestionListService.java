package com.nextPick.questionList.service;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.keyword.entity.Keyword;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.questionList.repository.QuestionListRepository;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionListService {
    private final QuestionListRepository questionListRepository;
//
    /**
     * 사용자 응답을 분석하고 판정하는 메서드
     *
     * @param questionListId    분석할 질문의 Id
     * @param userResponse      분석할 사용자 응답
     * @return 정답인지 아닌지 반환[임시]
     */
    public boolean judgementResponse(long questionListId, String userResponse) {
        QuestionList question = questionListRepository.findById(questionListId)
                .orElseThrow(()-> new BusinessLogicException(ExceptionCode.QUESTION_NOT_FOUND));
        List<String> keywordSet = question.getKeywords().stream()
                .map(Keyword::getWord)
                .collect(Collectors.toList());
        List<String> correctContexts = question.getKeywords().stream()
                .map(Keyword::getWordExplain)
                .collect(Collectors.toList());

        //키워드가 빠져 있는지 확인
        if(!areAllKeywordsPresent(userResponse,keywordSet))
            return false;

        //사용자 응답 분석 ( 형태소 분석 + 맥락 분석 + 단어 간 관계도)
        Map<String, Stack<String>> userResponseMorphemeAnalysis = userMorphemeAnalysis(userResponse,keywordSet);

        //DB 정답 분석 ( 형태소 분석 + 맥락 분석 + 단어 간 관계도)
        Map<String, Stack<String>> databaseCorrectMorphemeAnalysis = databaseMorphemeAnalysis(correctContexts,keywordSet);

        //사용자 응답과 DB 정답 비교 후 일치율 파악
        boolean isCorrect = matchRateScoring(databaseCorrectMorphemeAnalysis,userResponseMorphemeAnalysis,keywordSet);


//        Map<String, List<String>> extractedAttributes = extractKeywordsAttributes(userResponse, question.getKeywords());
//        boolean isCorrect = compareAttributes(extractedAttributes, question.getKeywords());
        return isCorrect;
    }
//
    /**
     * Komoran을 사용하여 키워드와 그 속성을 추출하는 메서드
     *
     * @param userResponse 분석할 문장
     * @param keywords 추출할 키워드 리스트
     * @return 키워드와 그 속성의 매핑
     */
    private Map<String, List<String>> extractKeywordsAttributes(String userResponse, List<Keyword> keywords) {
        Map<String, List<String>> keywordAttributes = new HashMap<>();
        List<String> keywordSet = keywords.stream()
                .map(Keyword::getWord)
                .collect(Collectors.toList());

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("src/main/resources/userCustomDic.txt");
        areAllKeywordsPresent(userResponse,keywordSet);
        // 형태소 분석
        KomoranResult analyzeResultList = komoran.analyze(userResponse);
        List<Token> tokens = analyzeResultList.getTokenList();

        // 키워드별 속성 추출
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            String morph = token.getMorph();
            String pos = token.getPos();

            // 키워드인지 확인
            if (keywordSet.contains(morph) && pos.equals("NNG")) {
                List<String> attributes = new ArrayList<>();
                StringBuilder attributeBuilder = new StringBuilder();

                // 키워드 다음에 오는 형용사(VV), 동사(VV), 형용사 관련 POS 추출
                for (int j = i + 1; j < tokens.size(); j++) {
                    Token nextToken = tokens.get(j);
                    String nextMorph = nextToken.getMorph();
                    String nextPos = nextToken.getPos();


                    if (nextPos.startsWith("VV") || nextPos.startsWith("VA") || nextPos.startsWith("VCP") || nextPos.startsWith("VX")) {
                        // 동사 또는 형용사인 경우 속성에 추가
                        attributeBuilder.append(nextMorph).append(" ");
                    } else if (nextPos.startsWith("J")) {
                        // 조사는 건너뛰고 다음 형태소로 계속 탐색
                        continue;
                    } else if (nextPos.equals("EC") || nextPos.equals("EF") || nextPos.equals("SF")) {
                        // 연결 어미, 종결 어미, 문장 부호가 나오면 속성 추출 종료
                        break;
                    } else {
                        // 다른 품사가 나오면 속성 추출 종료
                        break;
                    }
                }

                String attribute = attributeBuilder.toString().trim();
                if (!attribute.isEmpty()) {
                    attributes.add(attribute);
                    keywordAttributes.put(morph, attributes);
                }
            }
        }
        return keywordAttributes;
    }

    /**
     * 추출된 사용자 속성과 정답 속성을 비교하여 일치 여부를 판단하는 메서드
     *
     * @param userAttributes    사용자 응답에서 추출된 키워드 속성
     * @param keywords          정답 맥락 리스트
     * @return 모든 정답 맥락이 사용자 속성에 포함되면 true, 그렇지 않으면 false
     */
    private boolean compareAttributes(Map<String, List<String>> userAttributes, List<Keyword> keywords) {
        Set<String> correctContexts = keywords.stream()
                .map(Keyword::getWordExplain)
                .collect(Collectors.toSet());
        for (String context : correctContexts) {
            boolean matched = false;
            for (List<String> attrs : userAttributes.values()) {
                for (String attr : attrs) {
                    if (attr.contains(context)) {
                        matched = true;
                        break;
                    }
                }
                if (matched) break;
            }
            if (!matched) {
                return false;
            }
        }
        return true;
    }

//    public void komoranTestService(String s) {
//        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
//        komoran.setUserDic("./userCostomDic.dic");
//        String correctAnswer = "Immutable 객체는 상 태를 변경할 수 없는 객체를 의미합니다. " +
//                "이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. " +
//                "대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.";
//        String userAnswer = "Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. " +
//                "이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. " +
//                "대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.";
//
//        List<String> keywords = new ArrayList<>();
//        keywords.add("불변성");
//
//        List<String> keywordDescriptions = new ArrayList<>();
//        keywordDescriptions.add("객체를 수정할 수 없다.");
//        keywordDescriptions.add("객체를 변경할 수 없다.");
//
//        boolean result = checkCorrect(userAnswer,keywordDescriptions,keywords,komoran);
//
//        Stack<String> stack = new Stack<>();
//
//        for(String keyword : keywordDescriptions){
//            KomoranResult analyzeResultList = komoran.analyze(keyword);
//            List<Token> tokenList = analyzeResultList.getTokenList();
//            boolean doubleNNG = false;
//            for (int i = 0; i < tokenList.size(); i++){
//                Token token = tokenList.get(i);
//                // 분석해야 하는 형태소라면은..?
//                switch (token.getPos()){
//                    case "JKS": // 가
//                    case "JKO": // 를, 은, 는 '>'
//                        doubleNNG = false;
//                        stack.pop();
//                        stack.push(">"); break;
//                    case "JC":  // 와 '&'
//                        doubleNNG = false;
//                        stack.pop();
//                        stack.push("&"); break;
//                    case "JKB": // 에서, 에, 으로 '<'
//                        doubleNNG = false;
//                        stack.pop();
//                        stack.push("<"); break;
//                    case "VV":  // 동사
//                    case "VA":  // 형용사
//                        doubleNNG = false;
//                        stack.push(token.getMorph());
//                        stack.push("/");
//                        break;
//                    case "NNG": // 일반 명사
//                    case "NNP": // 고유 명사
//                    case "SL":  // 외국어
//                        if(doubleNNG)
//                            stack.pop();
//                        stack.push(token.getMorph());
//                        stack.push("/");
//                        doubleNNG = true;
//                        break;
//                    default:
//                        doubleNNG = false;
//                }
//            }
//            System.out.format("[keywordDescription-translation] %s\n", stack);
//            System.out.println(keyword);
//        }
//
//
//    }
    private Map<String,Stack<String>> databaseMorphemeAnalysis(List<String> wordExplains, List<String> keywordList) {
        Stack<String> stack = new Stack<>();
        Map<String,Stack<String>> result = new HashMap<>();

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("src/main/resources/userCustomDic.txt");
        for (int i = 0; i < keywordList.size(); i++) {
            if(wordExplains.get(i).isEmpty())
                continue;
            KomoranResult analyzeResultList = komoran.analyze(wordExplains.get(i));
            List<Token> tokenList = analyzeResultList.getTokenList();
            for (int j = 0; j < tokenList.size(); j++){
                Token token = tokenList.get(i);
                // 분석해야 하는 형태소라면은..?
                switch (token.getPos()) {
                    case "JKS": // 가
                    case "JKO": // 를, 은, 는 '>'
                        stack.pop();
                        stack.push(">");
                        break;
                    case "JC":  // 와 '&'
                        stack.pop();
                        stack.push("&");
                        break;
                    case "JKB": // 에서, 에, 으로 '<'
                        stack.pop();
                        stack.push("<");
                        break;
                    case "VV":  // 동사
                    case "VA":  // 형용사
                        stack.push(token.getMorph());
                        stack.push("/");
                        break;
                    case "NNG": // 일반 명사
                    case "NNP": // 고유 명사
                    case "SL":  // 외국어
                        stack.push(token.getMorph());
                        stack.push("/");
                        break;
                    case "EF": // 문장의 끝. 디버그용으로 텍스트 출력
                        break;
                    default:
                }
            }
            System.out.println("[databaseMorphemeAnalysis] sentenceTitle : "+keywordList.get(i));
            System.out.println("[databaseMorphemeAnalysis] queue : "+stack);
            result.put(keywordList.get(i),stack);
            stack.clear();
        }
        return result;
    }

    private Map<String,Stack<String>> userMorphemeAnalysis(String userAnswer, List<String> keywordList) {
        Stack<String> stack = new Stack<>();
        Map<String,Stack<String>> result = new HashMap<>();

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("src/main/resources/userCustomDic.txt");

        KomoranResult analyzeResultList = komoran.analyze(userAnswer);
        List<Token> tokenList = analyzeResultList.getTokenList();

        String sentenceTitle = "";
        for (int i = 0; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            if(keywordList.contains(token.getMorph()))
                sentenceTitle = token.getMorph();
            // 분석해야 하는 형태소라면은..?
            switch (token.getPos()) {
                case "JKS": // 가
                case "JKO": // 를, 은, 는 '>'
                    stack.pop();
                    stack.push(">");
                    break;
                case "JC":  // 와 '&'
                    stack.pop();
                    stack.push("&");
                    break;
                case "JKB": // 에서, 에, 으로 '<'
                    stack.pop();
                    stack.push("<");
                    break;
                case "VV":  // 동사
                case "VA":  // 형용사
                    stack.push(token.getMorph());
                    stack.push("/");
                    break;
                case "NNG": // 일반 명사
                case "NNP": // 고유 명사
                case "SL":  // 외국어
                    stack.push(token.getMorph());
                    stack.push("/");
                    break;
                case "EF": // 문장의 끝. 디버그용으로 텍스트 출력
                    if (result.containsKey("A")) {
                        Stack<String> existingStack = result.get("A");
                        existingStack.push("4");
                        existingStack.push("5");
                    }
                    System.out.println("[userMorphemeAnalysis] sentenceTitle : "+sentenceTitle);
                    System.out.println("[userMorphemeAnalysis] queue : "+stack);
                    result.put(sentenceTitle,stack);
                    stack.clear();
                    break;
                default:
            }
        }
        return result;
    }

    private boolean matchRateScoring(Map<String,Stack<String>> dbAnswer, Map<String,Stack<String>> userAnswer,List<String> keywords) {
        for (String keyword : keywords) {
            Stack<String> dbAnswerStack = dbAnswer.get(keyword);
            Stack<String> userAnswerStack = userAnswer.get(keyword);
            String checkStr = dbAnswerStack.peek();
            for (int i = 0; i < userAnswerStack.size(); i++) {
                if(checkStr.equals(userAnswerStack.get(i))){
                    dbAnswerStack.pop();
                    try{
                        checkStr = dbAnswerStack.peek();
                    }catch (EmptyStackException e) {
                        System.out.println("[matchRateScoring] 스택이 비어 있습니다! 빈 스택에서 peek()를 호출할 수 없습니다.");
                        break;
                    }
                    i = 0;
                } else if (i == userAnswerStack.size() - 1) {
                    System.out.println("[matchRateScoring] 미흡한 설명이 존재합니다.");
                    return false;
                }
            }
        }
        return true;
    }
//
//    private boolean checkCorrect(String userAnswer,List<String> keywordDescriptions,List<String> keywords,Komoran komoran){
//        List<String> userAnswerSplitString = morphemeAnalysis(userAnswer,komoran);
//        boolean keywordsCheckPass = areAllKeywordsPresent(userAnswer,keywords);
////        boolean keywordDescriptionsCheckPass = areAllElementsPresent(userAnswerSplitString,);
//        return true;
//    }
//
    /**
     * 문자열에 모든 키워드가 포함되어 있는지 확인하는 메서드
     *
     * @param userAnswer      확인할 대상 문자열
     * @param keywords 키워드 리스트
     * @return 모든 키워드가 포함되어 있으면 true, 그렇지 않으면 false
     */
    public boolean areAllKeywordsPresent(String userAnswer, List<String> keywords) {
        for (String keyword : keywords) {
            if (!userAnswer.contains(keyword)) {
                // 하나라도 포함되지 않으면 즉시 false 반환
                return false;
            }
        }
        // 모든 키워드가 포함되었음을 확인
        return true;
    }

    // 스택을 한꺼번에 합치는 메서드
    public static void mergeStacks(Stack<String> stack1, Stack<String> stack2) {
        // stack2의 모든 요소를 stack1에 push (역순으로 추가)
        Stack<String> tempStack = new Stack<>(); // 중간에 뒤집기 위한 임시 스택
        while (!stack2.isEmpty()) {
            tempStack.push(stack2.pop());
        }
        while (!tempStack.isEmpty()) {
            stack1.push(tempStack.pop());
        }
    }
//
//    /**
//     * List B의 모든 요소가 List A에 포함되어 있는지 확인하는 메서드
//     *
//     * @param listA 확인 대상 리스트 A
//     * @param listB 확인할 리스트 B
//     * @return 모든 요소가 포함되어 있으면 true, 그렇지 않으면 false
//     */
//    public boolean areAllElementsPresent(List<String> listA, List<String> listB) {
//        // List A를 HashSet으로 변환하여 빠른 검색 가능하게 함
//        Set<String> setA = new HashSet<>(listA);
//
//        // List B의 각 요소가 Set A에 포함되어 있는지 확인
//        for (String str : listB) {
//            if (!setA.contains(str)) {
//                // 하나라도 포함되지 않으면 즉시 false 반환
//                return false;
//            }
//        }
//
//        // 모든 요소가 포함되어 있음을 확인
//        return true;
//    }
}
