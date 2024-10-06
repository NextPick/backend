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
        Map<String, Stack<String>> userResponseMorpheme = userMorphemeAnalysis(userResponse,keywordSet);

        //DB 정답 분석 ( 형태소 분석 + 맥락 분석 + 단어 간 관계도)
        Map<String, Stack<String>> databaseCorrectMorpheme = databaseMorphemeAnalysis(correctContexts,keywordSet);

        //사용자 응답 심화 분석 ( 단어 간 관계도 역전 및 정규화 )
        Map<String, Stack<String>> userResponseDeep = userResponseDeepAnalysis(userResponseMorpheme);

        //DB 정답 심화 분석 ( 단어 간 관계도 역전 및 정규화 )
        Map<String, Stack<String>> databaseResponseDeep = userResponseDeepAnalysis(databaseCorrectMorpheme);

        //사용자 응답과 DB 정답 비교 후 일치율 파악
        boolean isCorrect = matchRateScoring(databaseResponseDeep,userResponseDeep,keywordSet);


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

    private Map<String,Stack<String>> databaseMorphemeAnalysis(List<String> wordExplains, List<String> keywordList) {
        Stack<String> stack = new Stack<>();
        Map<String,Stack<String>> result = new HashMap<>();

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("src/main/resources/userCustomDic.txt");
        for (int i = 0; i < keywordList.size(); i++) {
            if(wordExplains.get(i) == null)
                continue;
            KomoranResult analyzeResultList = komoran.analyze(wordExplains.get(i));
            List<Token> tokenList = analyzeResultList.getTokenList();
            for (int j = 0; j < tokenList.size(); j++){
                Token token = tokenList.get(j);
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
            Stack<String> newStack = new Stack<>();
            newStack.addAll(stack); // 기존 stack의 내용을 새로운 stack에 복사
            stack.clear();
            System.out.println("\u001B[34m" + "[databaseMorphemeAnalysis] sentenceTitle : " + keywordList.get(i));
            // key값이 중복된다면.. 합친다.
            if (result.containsKey(keywordList.get(i))) {
                Stack<String> existingStack = result.get(keywordList.get(i));
                stack = mergeStacks(existingStack,newStack);
                System.out.println("[databaseMorphemeAnalysis] stack : " + stack + "\u001B[0m");
                result.put(keywordList.get(i),stack);
            }else{
                System.out.println("[databaseMorphemeAnalysis] stack : " + newStack + "\u001B[0m");
                result.put(keywordList.get(i),newStack);
            }
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

        List<String> sentenceTitles = new ArrayList<>();
        for (int i = 0; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            if(keywordList.contains(token.getMorph())) {
                if(!sentenceTitles.contains(token.getMorph()))
                    sentenceTitles.add(token.getMorph());
            }
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
                    Stack<String> newStack = new Stack<>();
                    newStack.addAll(stack); // 기존 stack의 내용을 새로운 stack에 복사
                    stack.clear();
                    // key값이 중복된다면.. 합친다.
                    for(String sentenceTitle : sentenceTitles){
                        System.out.println("\u001B[32m" + "[userMorphemeAnalysis] sentenceTitle : " + sentenceTitle );
                        if (result.containsKey(sentenceTitle)) {
                            Stack<String> existingStack = result.get(sentenceTitle);
                            stack = mergeStacks(existingStack,newStack);
                            System.out.println("[userMorphemeAnalysis] stack : " + stack + "\u001B[0m");
                            result.put(sentenceTitle,stack);
                        }else{
                            System.out.println("[userMorphemeAnalysis] stack : " + newStack + "\u001B[0m");
                            result.put(sentenceTitle,newStack);
                        }
                    }
                    sentenceTitles.clear();
                    break;
                default:
            }
        }
        return result;
    }

    private Map<String,Stack<String>> userResponseDeepAnalysis(Map<String,Stack<String>> userResponseMorpheme) {
        Stack<String> deepAnalysis = new Stack<>();
        Map<String,Stack<String>> result = new HashMap<>();

        for (Map.Entry<String, Stack<String>> entry : userResponseMorpheme.entrySet()) {
            String key = entry.getKey();
            Stack<String> originalStack = entry.getValue();

            // 1. 기본 변환 수행
            Stack<String> transformedStack = transformStack(originalStack);

            // 2. 심화 변환 수행
            Stack<String> deepTransformedStack = deepTransformStack(transformedStack);

            // 결과 맵에 추가
            result.put(key, deepTransformedStack);
        }
        return result;
    }

    /**
     * 주어진 Stack<String>을 변환하여 새로운 Stack<String>을 반환합니다.
     *
     * 변환 규칙:
     * - "/"를 만나면 현재까지 모은 단어들을 하나의 단어로 합칩니다.
     * - ">" 또는 "<"를 만나면 이전 단어와 결합하여 새로운 단어로 만듭니다.
     *
     * @param originalStack 원본 스택
     * @return 변환된 스택
     */
    private Stack<String> transformStack(Stack<String> originalStack) {
        Stack<String> resultStack = new Stack<>();
        StringBuilder currentWord = new StringBuilder();
        boolean insideAngleBrackets = false;

        // Stack을 리스트로 변환하여 순차적으로 접근
        Iterator<String> iterator = originalStack.iterator();

        while (iterator.hasNext()) {
            String token = iterator.next();

            switch (token) {
                case "/":
                    if (insideAngleBrackets) {
                        // '<'와 '>' 사이에서는 '/'를 쉼표로 대체
                        currentWord.append(", ");
                    } else {
                        // '/'는 단어 구분자로 사용
                        if (currentWord.length() > 0) {
                            resultStack.push(currentWord.toString());
                            currentWord.setLength(0); // StringBuilder 초기화
                        }
                    }
                    break;

                case "<":
                case ">":
                    if (token.equals("<")) {
                        insideAngleBrackets = true;
                    } else if (token.equals(">")) {
                        insideAngleBrackets = false;
                    }
                    // '<' 또는 '>'를 현재 단어에 추가
                    currentWord.append(token);
                    break;

                default:
                    // 일반 단어는 현재 단어에 추가
                    currentWord.append(token);
                    break;
            }
        }

        // 마지막으로 남은 단어가 있다면 스택에 추가
        if (currentWord.length() > 0) {
            resultStack.push(currentWord.toString());
        }

        System.out.println("[transformStack]" + resultStack);
        return resultStack;
    }

    /**
     * 변환된 Stack<String>을 심화 분석하여 추가적인 변환을 수행합니다.
     *
     * 심화 변환 규칙:
     * 1. 문자열에 '<' 또는 '>'가 두 개 이상 포함된 경우 분해.
     *    예: "keyword<field>선언" → "keyword<field", "field>선언"
     * 2. 문자열이 "없"인 경우, 이전 문자열을 분해.
     *    예: ["상태>변경", "없"] → ["상태>변경", "변경", "없"]
     *
     * @param transformedStack 변환된 스택
     * @return 심화 변환된 스택
     */
    private Stack<String> deepTransformStack(Stack<String> transformedStack) {
        Stack<String> resultStack = new Stack<>();
        List<String> tokens = new ArrayList<>(transformedStack);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            // 1. 문자열에 '<' 또는 '>'가 두 개 이상 포함된 경우 분해
            if ((countOccurrences(token, "<") + countOccurrences(token, ">")) >= 2) {
                String[] splitParts = splitAtLastSymbol(token);
                if (splitParts != null) {
                    resultStack.push(splitParts[0]);
                    resultStack.push(splitParts[1]);
                    continue;
                }
            }

            // 2. 현재 토큰이 "없"또는 "있" 인 경우 이전 토큰을 분해
            if ((token.equals("없") || token.equals("있")) && !resultStack.isEmpty()) {
                String previous = resultStack.pop();
                String[] splitParts = splitAtLastSymbolForEmpty(previous);
                if (splitParts != null) {
                    resultStack.push(splitParts[0]); // 전체 이전 토큰
                    resultStack.push(splitParts[1]); // 분해된 부분
                }
                resultStack.push(token); // "없" 또는 "있" 추가
                continue;
            }

            // 기본적으로 토큰을 추가
            resultStack.push(token);
        }

        System.out.println("[deepTransformStack]" + resultStack);
        return resultStack;
    }

    /**
     * 문자열 내 특정 문자열의 개수를 셉니다.
     *
     * @param str 문자열
     * @param sub 서브 문자열
     * @return 개수
     */
    private int countOccurrences(String str, String sub) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    /**
     * 문자열 내 마지막 '<' 또는 '>' 기호에서 분리합니다.
     *
     * 예: "keyword<field>선언" → ["keyword<field", "field>선언"]
     *
     * @param str 문자열
     * @return 분리된 두 부분의 배열
     */
    private String[] splitAtLastSymbol(String str) {
        int lastLt = str.lastIndexOf("<");
        int lastGt = str.lastIndexOf(">");

        if (lastLt == -1 && lastGt == -1) {
            return null;
        }

        if (lastGt > lastLt) {
            // 마지막 기호가 '>'
            int lastLtBeforeGt = str.lastIndexOf("<", lastGt);
            if (lastLtBeforeGt == -1) {
                // '<'가 없으면 '>'에서만 분리
                String firstPart = str.substring(0, lastGt);
                String secondPart = str.substring(lastGt, str.length());
                return new String[]{firstPart, secondPart};
            } else {
                String firstPart = str.substring(0, lastGt);
                String secondPart = str.substring(lastLtBeforeGt + 1, str.length());
                return new String[]{firstPart, secondPart};
            }
        } else {
            // 마지막 기호가 '<'인 경우
            int lastGtBeforeLt = str.lastIndexOf(">", lastLt);
            if (lastGtBeforeLt == -1) {
                // '>'가 없으면 '<'에서만 분리
                String firstPart = str.substring(0, lastLt);
                String secondPart = str.substring(lastLt, str.length());
                return new String[]{firstPart, secondPart};
            } else {
                // 다른 경우는 현재 로직으로 처리하지 않음
                return null;
            }
        }
    }

    /**
     * "없"인 경우 이전 문자열을 분해합니다.
     * 예: "상태>변경" → ["상태>변경", "변경"]
     *
     * @param str 이전 문자열
     * @return 분리된 두 부분의 배열
     */
    private String[] splitAtLastSymbolForEmpty(String str) {
        // 예: "상태>변경" → "상태>변경", "변경"
        int lastGt = str.lastIndexOf(">");
        int lastLt = str.lastIndexOf("<");

        if (lastGt == -1 && lastLt == -1) {
            return null;
        }

        if (lastGt > lastLt) {
            // 마지막 기호가 '>'
            String firstPart = str; // 전체 문자열 유지
            String secondPart = str.substring(lastGt + 1, str.length());
            if (secondPart.isEmpty()) {
                // '>' 다음에 아무것도 없으면 분리하지 않음
                return null;
            }
            return new String[]{firstPart, secondPart};
        } else {
            // 마지막 기호가 '<'
            String firstPart = str;
            String secondPart = str.substring(lastLt + 1, str.length());
            if (secondPart.isEmpty()) {
                return null;
            }
            return new String[]{firstPart, secondPart};
        }
    }

    private boolean matchRateScoring(Map<String,Stack<String>> dbAnswer, Map<String,Stack<String>> userAnswer,List<String> keywords) {
        for (String keyword : keywords) {
            Stack<String> dbAnswerStack = dbAnswer.get(keyword);
            if(dbAnswerStack == null)
                continue;
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
    private Stack<String> mergeStacks(Stack<String> stackBase, Stack<String> stackAdd) {
        Stack<String> stackAddClone = new Stack<>();
        Stack<String> tempStack = new Stack<>(); // 중간에 뒤집기 위한 임시 스택
        stackAddClone.addAll(stackAdd);
        while (!stackAddClone.isEmpty()) {
            tempStack.push(stackAddClone.pop());
        }
        while (!tempStack.isEmpty()) {
            stackBase.push(tempStack.pop());
        }
        return stackBase;
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
