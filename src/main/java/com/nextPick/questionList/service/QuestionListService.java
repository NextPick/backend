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
//    private final Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
//
//    /**
//     * 사용자 응답을 분석하고 판정하는 메서드
//     *
//     * @param question      분석할 질문
//     * @param userResponse  분석할 사용자 응답
//     */
//    public void evaluateResponse(Question question, UserResponse userResponse) {
//        Map<String, List<String>> extractedAttributes = extractKeywordsAttributes(userResponse.getResponseText(), question.getKeywords());
//        boolean isCorrect = compareAttributes(extractedAttributes, question.getContexts());
//        userResponse.setCorrect(isCorrect);
//    }
//
//    /**
//     * Komoran을 사용하여 키워드와 그 속성을 추출하는 메서드
//     *
//     * @param sentence 분석할 문장
//     * @param keywords 추출할 키워드 리스트
//     * @return 키워드와 그 속성의 매핑
//     */
//    private Map<String, List<String>> extractKeywordsAttributes(String sentence, List<String> keywords) {
//        Map<String, List<String>> keywordAttributes = new HashMap<>();
//        Set<String> keywordSet = new HashSet<>(keywords);
//
//        // 형태소 분석
//        List<Token> tokens = komoran.analyze(sentence);
//
//        // 키워드별 속성 추출
//        for (int i = 0; i < tokens.size(); i++) {
//            Token token = tokens.get(i);
//            String morph = token.getMorph();
//            String pos = token.getPos();
//
//            // 키워드인지 확인
//            if (keywordSet.contains(morph) && pos.equals("NNG")) {
//                List<String> attributes = new ArrayList<>();
//                StringBuilder attributeBuilder = new StringBuilder();
//
//                // 키워드 다음에 오는 형용사(VV), 동사(VV), 형용사 관련 POS 추출
//                for (int j = i + 1; j < tokens.size(); j++) {
//                    Token nextToken = tokens.get(j);
//                    String nextMorph = nextToken.getMorph();
//                    String nextPos = nextToken.getPos();
//
//                    if (nextPos.startsWith("VV") || nextPos.startsWith("VCP") || nextPos.startsWith("VX")) { // 동사, 형용사 관련 POS
//                        attributeBuilder.append(nextMorph).append(" ");
//                    } else if (nextPos.equals("EC") || nextPos.startsWith("J")) { // 연결 어미 또는 조사
//                        // 속성 표현 종료
//                        break;
//                    } else {
//                        // 다른 품사 태그가 나오면 속성 추출 종료
//                        break;
//                    }
//                }
//
//                String attribute = attributeBuilder.toString().trim();
//                if (!attribute.isEmpty()) {
//                    attributes.add(attribute);
//                    keywordAttributes.put(morph, attributes);
//                }
//            }
//        }
//
//        return keywordAttributes;
//    }
//
//    /**
//     * 추출된 사용자 속성과 정답 속성을 비교하여 일치 여부를 판단하는 메서드
//     *
//     * @param userAttributes    사용자 응답에서 추출된 키워드 속성
//     * @param correctContexts 정답 맥락 리스트
//     * @return 모든 정답 맥락이 사용자 속성에 포함되면 true, 그렇지 않으면 false
//     */
//    private boolean compareAttributes(Map<String, List<String>> userAttributes, List<String> correctContexts) {
//        for (String context : correctContexts) {
//            boolean matched = false;
//            for (List<String> attrs : userAttributes.values()) {
//                for (String attr : attrs) {
//                    if (attr.contains(context)) {
//                        matched = true;
//                        break;
//                    }
//                }
//                if (matched) break;
//            }
//            if (!matched) {
//                return false;
//            }
//        }
//        return true;
//    }

//    public void komoranTestService(String s) {
//        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
////        komoran.setUserDic("./userCostomDic.dic");
//        String correctAnswer = "Immutable 객체는 상태를 변경할 수 없는 객체를 의미합니다. " +
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
//    private List<String> morphemeAnalysis(String str, Komoran komoran){
//        Stack<String> stack = new Stack<>();
//        List<String> splitList = new ArrayList<>();
//        List<String> trimAnswerText = new ArrayList<>(Arrays.asList(str.split("[.?!]")));
//        for(String answerTextSingle : trimAnswerText) {
//            KomoranResult analyzeResultList = komoran.analyze(answerTextSingle);
//            List<Token> tokenList = analyzeResultList.getTokenList();
//            boolean doubleNNG = false;
//            for (int i = 0; i < tokenList.size(); i++) {
//                Token token = tokenList.get(i);
//                // 분석해야 하는 형태소라면은..?
//                switch (token.getPos()) {
//                    case "JKS": // 가
//                    case "JKO": // 를, 은, 는 '>'
//                        doubleNNG = false;
//                        stack.pop();
//                        stack.push(">");
//                        break;
//                    case "JC":  // 와 '&'
//                        doubleNNG = false;
//                        stack.pop();
//                        stack.push("&");
//                        break;
//                    case "JKB": // 에서, 에, 으로 '<'
//                        doubleNNG = false;
//                        stack.pop();
//                        stack.push("<");
//                        break;
//                    case "VV":  // 동사
//                    case "VA":  // 형용사
//                        doubleNNG = false;
//                        stack.push(token.getMorph());
//                        stack.push("/");
//                        break;
//                    case "NNG": // 일반 명사
//                    case "NNP": // 고유 명사
//                    case "SL":  // 외국어
//                        if (doubleNNG)
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
//            System.out.println(answerTextSingle);
//
//            StringBuilder sb = new StringBuilder();
//            while (!stack.isEmpty()) {
//                sb.insert(0, stack.pop()); // 스택은 LIFO이므로 순서를 유지하기 위해 앞에 삽입
//            }
//            String combinedStr = sb.toString();
//            String[] splitArray = combinedStr.split("/");
//            for (String s : splitArray) {
//                if (!s.isEmpty()) { // 빈 문자열이 아닐 경우에만 추가
//                    splitList.add(s);
//                }
//            }
//        }
//        return splitList;
//    }
//
//    private boolean checkCorrect(String userAnswer,List<String> keywordDescriptions,List<String> keywords,Komoran komoran){
//        List<String> userAnswerSplitString = morphemeAnalysis(userAnswer,komoran);
//        boolean keywordsCheckPass = areAllKeywordsPresent(userAnswer,keywords);
////        boolean keywordDescriptionsCheckPass = areAllElementsPresent(userAnswerSplitString,);
//        return true;
//    }
//
//    /**
//     * 문자열에 모든 키워드가 포함되어 있는지 확인하는 메서드
//     *
//     * @param str      확인할 대상 문자열
//     * @param keywords 키워드 리스트
//     * @return 모든 키워드가 포함되어 있으면 true, 그렇지 않으면 false
//     */
//    public boolean areAllKeywordsPresent(String str, List<String> keywords) {
//        for (String keyword : keywords) {
//            if (!str.contains(keyword)) {
//                // 하나라도 포함되지 않으면 즉시 false 반환
//                return false;
//            }
//        }
//        // 모든 키워드가 포함되었음을 확인
//        return true;
//    }
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
