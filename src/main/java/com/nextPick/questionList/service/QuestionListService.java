package com.nextPick.questionList.service;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionListService {

    public void komoranTestService(String s) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
//        komoran.setUserDic("./userCostomDic.dic");
        String correctAnswer = "Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. " +
                "이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. " +
                "대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.";
        String userAnswer = "Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. " +
                "이러한 객체는 생성된 이후 내부 상태를 수정할 수 없으며, 주로 final keyword로 field를 선언하여 불변성을 보장합니다. " +
                "대표적인 예로 String class가 있으며, 이 객체는 한 번 생성되면 변경할 수 없습니다.";

        List<String> keywords = new ArrayList<>();
        keywords.add("불변성");

        List<String> keywordDescriptions = new ArrayList<>();
        keywordDescriptions.add("객체를 수정할 수 없다.");
        keywordDescriptions.add("객체를 변경할 수 없다.");

        boolean result = checkCorrect(userAnswer,keywordDescriptions,keywords,komoran);

        Stack<String> stack = new Stack<>();

        for(String keyword : keywordDescriptions){
            KomoranResult analyzeResultList = komoran.analyze(keyword);
            List<Token> tokenList = analyzeResultList.getTokenList();
            boolean doubleNNG = false;
            for (int i = 0; i < tokenList.size(); i++){
                Token token = tokenList.get(i);
                // 분석해야 하는 형태소라면은..?
                switch (token.getPos()){
                    case "JKS": // 가
                    case "JKO": // 를, 은, 는 '>'
                        doubleNNG = false;
                        stack.pop();
                        stack.push(">"); break;
                    case "JC":  // 와 '&'
                        doubleNNG = false;
                        stack.pop();
                        stack.push("&"); break;
                    case "JKB": // 에서, 에, 으로 '<'
                        doubleNNG = false;
                        stack.pop();
                        stack.push("<"); break;
                    case "VV":  // 동사
                    case "VA":  // 형용사
                        doubleNNG = false;
                        stack.push(token.getMorph());
                        stack.push("/");
                        break;
                    case "NNG": // 일반 명사
                    case "NNP": // 고유 명사
                    case "SL":  // 외국어
                        if(doubleNNG)
                            stack.pop();
                        stack.push(token.getMorph());
                        stack.push("/");
                        doubleNNG = true;
                        break;
                    default:
                        doubleNNG = false;
                }
            }
            System.out.format("[keywordDescription-translation] %s\n", stack);
            System.out.println(keyword);
        }


    }
    private List<String> morphemeAnalysis(String str, Komoran komoran){
        Stack<String> stack = new Stack<>();
        List<String> splitList = new ArrayList<>();
        List<String> trimAnswerText = new ArrayList<>(Arrays.asList(str.split("[.?!]")));
        for(String answerTextSingle : trimAnswerText) {
            KomoranResult analyzeResultList = komoran.analyze(answerTextSingle);
            List<Token> tokenList = analyzeResultList.getTokenList();
            boolean doubleNNG = false;
            for (int i = 0; i < tokenList.size(); i++) {
                Token token = tokenList.get(i);
                // 분석해야 하는 형태소라면은..?
                switch (token.getPos()) {
                    case "JKS": // 가
                    case "JKO": // 를, 은, 는 '>'
                        doubleNNG = false;
                        stack.pop();
                        stack.push(">");
                        break;
                    case "JC":  // 와 '&'
                        doubleNNG = false;
                        stack.pop();
                        stack.push("&");
                        break;
                    case "JKB": // 에서, 에, 으로 '<'
                        doubleNNG = false;
                        stack.pop();
                        stack.push("<");
                        break;
                    case "VV":  // 동사
                    case "VA":  // 형용사
                        doubleNNG = false;
                        stack.push(token.getMorph());
                        stack.push("/");
                        break;
                    case "NNG": // 일반 명사
                    case "NNP": // 고유 명사
                    case "SL":  // 외국어
                        if (doubleNNG)
                            stack.pop();
                        stack.push(token.getMorph());
                        stack.push("/");
                        doubleNNG = true;
                        break;
                    default:
                        doubleNNG = false;
                }
            }
            System.out.format("[keywordDescription-translation] %s\n", stack);
            System.out.println(answerTextSingle);

            StringBuilder sb = new StringBuilder();
            while (!stack.isEmpty()) {
                sb.insert(0, stack.pop()); // 스택은 LIFO이므로 순서를 유지하기 위해 앞에 삽입
            }
            String combinedStr = sb.toString();
            String[] splitArray = combinedStr.split("/");
            for (String s : splitArray) {
                if (!s.isEmpty()) { // 빈 문자열이 아닐 경우에만 추가
                    splitList.add(s);
                }
            }
        }
        return splitList;
    }

    private boolean checkCorrect(String userAnswer,List<String> keywordDescriptions,List<String> keywords,Komoran komoran){
        List<String> userAnswerSplitString = morphemeAnalysis(userAnswer,komoran);
        boolean keywordsCheckPass = areAllKeywordsPresent(userAnswer,keywords);
//        boolean keywordDescriptionsCheckPass = areAllElementsPresent(userAnswerSplitString,);
        return true;
    }

    /**
     * 문자열에 모든 키워드가 포함되어 있는지 확인하는 메서드
     *
     * @param str      확인할 대상 문자열
     * @param keywords 키워드 리스트
     * @return 모든 키워드가 포함되어 있으면 true, 그렇지 않으면 false
     */
    public boolean areAllKeywordsPresent(String str, List<String> keywords) {
        for (String keyword : keywords) {
            if (!str.contains(keyword)) {
                // 하나라도 포함되지 않으면 즉시 false 반환
                return false;
            }
        }
        // 모든 키워드가 포함되었음을 확인
        return true;
    }

    /**
     * List B의 모든 요소가 List A에 포함되어 있는지 확인하는 메서드
     *
     * @param listA 확인 대상 리스트 A
     * @param listB 확인할 리스트 B
     * @return 모든 요소가 포함되어 있으면 true, 그렇지 않으면 false
     */
    public boolean areAllElementsPresent(List<String> listA, List<String> listB) {
        // List A를 HashSet으로 변환하여 빠른 검색 가능하게 함
        Set<String> setA = new HashSet<>(listA);

        // List B의 각 요소가 Set A에 포함되어 있는지 확인
        for (String str : listB) {
            if (!setA.contains(str)) {
                // 하나라도 포함되지 않으면 즉시 false 반환
                return false;
            }
        }

        // 모든 요소가 포함되어 있음을 확인
        return true;
    }
}
