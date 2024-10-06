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
import kr.co.shineware.nlp.komoran.modeler.model.Observation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;
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

        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("src/main/resources/userCustomDic.txt");

        //키워드가 빠져 있는지 확인
        if(!areAllKeywordsPresent(userResponse,keywordSet)){
            System.out.println("[areAllKeywordsPresent] 누락된 키워드가 존재합니다.");
            return false;
        }

        //사용자 응답 분석 ( 형태소 분석 + 맥락 분석 + 단어 간 관계도)
        Map<String, Stack<String>> userResponseMorpheme = userMorphemeAnalysis(userResponse,keywordSet,komoran);

        //DB 정답 분석 ( 형태소 분석 + 맥락 분석 + 단어 간 관계도)
        Map<String, Stack<String>> databaseCorrectMorpheme = databaseMorphemeAnalysis(correctContexts,keywordSet,komoran);

        //사용자 응답 심화 분석 ( 단어 간 관계도 정규화 및 해석 불필요 단어 제거 )
        Map<String, Stack<String>> userResponseDeep = userResponseDeepAnalysis(userResponseMorpheme);

        //DB 정답 심화 분석 ( 단어 간 관계도 정규화 및 해석 불필요 단어 제거 )
        Map<String, Stack<String>> databaseResponseDeep = userResponseDeepAnalysis(databaseCorrectMorpheme);

        //사용자 응답 인식/일치율 증가 작업 ( 단어 간 관계도 역전 )
        Map<String, Stack<String>> userResponseRelateReverse = userResponseRelateReverseAnalysis(userResponseDeep);

        //분석 결과 확인 목적 디버그 메서드
        debugFinalMorphemeAnalysis(databaseResponseDeep,userResponseRelateReverse);

        //사용자 응답과 DB 정답 비교 후 일치율 파악
        boolean isCorrect = matchRateScoring(databaseResponseDeep,userResponseRelateReverse,keywordSet);

        return isCorrect;
    }
//
    private void debugFinalMorphemeAnalysis(Map<String, Stack<String>> databaseResponseDeep,
                                            Map<String, Stack<String>> userResponseRelateReverse) {

        System.out.println("--------------------------------------------------------------------");
        for (Map.Entry<String, Stack<String>> entry : databaseResponseDeep.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("\u001b[36;1m" + "[final-DB_Response] key : " + key );
            System.out.println("[final-DB_Response] values : " + values + "\u001B[0m");
            System.out.println("--------------------------------------------------------------------");
        }

        for (Map.Entry<String, Stack<String>> entry : userResponseRelateReverse.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            System.out.println("\u001b[34;1m" + "[final-User_Response] key : " + key );
            System.out.println("[final-User_Response] values : " + values + "\u001B[0m");
            System.out.println("--------------------------------------------------------------------");
        }
    }

    private Map<String,Stack<String>> databaseMorphemeAnalysis(List<String> wordExplains,
                                                               List<String> keywordList,
                                                               Komoran komoran) {
        Stack<String> stack = new Stack<>();
        Map<String,Stack<String>> result = new HashMap<>();

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

    private Map<String,Stack<String>> userMorphemeAnalysis(String userAnswer,
                                                           List<String> keywordList,
                                                           Komoran komoran) {
        Stack<String> stack = new Stack<>();
        Map<String,Stack<String>> result = new HashMap<>();

        KomoranResult analyzeResultList = komoran.analyze(userAnswer);
        List<Token> tokenList = analyzeResultList.getTokenList();

        List<String> sentenceTitles = new ArrayList<>();
        for (int i = 0; i < tokenList.size(); i++) {
            Token token = tokenList.get(i);
            if(keywordList.contains(token.getMorph())) {
                if(!sentenceTitles.contains(token.getMorph())) {
                    sentenceTitles.add(token.getMorph());
                }
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
                            Stack<String> mergeStack = new Stack<>();
                            mergeStack = mergeStacks(existingStack,newStack);
                            System.out.println("[userMorphemeAnalysis] stack : " + mergeStack + "\u001B[0m");
                            result.put(sentenceTitle,mergeStack);
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

            // 1. 영문 대소문자 구분 제거 처리
            Stack<String> transformedEngStack = transformEngStack(originalStack);

            // 2. 동의어 처리
//            Stack<String> transformedSynonymsStack = transformSynonymsStack(originalStack);

            // 3. 기본 변환 수행 ( 불필요한 Stack 제거 )
            Stack<String> transformedStack = transformStack(transformedEngStack);

            // 4. 1차 심화 변환 수행 ( 1차 정규화 및 인식 개선 )
            Stack<String> deepTransformedStack = firstDeepTransformStack(transformedStack);

            // 5. 2차 심화 변환 수행 ( 2차 정규화 )
            Stack<String> secondDeepTransformedStack = secondDeepTransformStack(deepTransformedStack);

            // 결과 맵에 추가
            result.put(key, secondDeepTransformedStack);
        }
        return result;
    }

    private Map<String,Stack<String>> userResponseRelateReverseAnalysis(Map<String,Stack<String>> userResponseDeep) {
        Map<String,Stack<String>> result = new HashMap<>();

        for (Map.Entry<String, Stack<String>> entry : userResponseDeep.entrySet()) {
            String key = entry.getKey();
            Stack<String> originalStack = entry.getValue();

            // 1. 단어간 연관 관계 역전 변환 수행 ( 불필요한 Stack 제거 )
            Stack<String> transformedStack = transformStackRelateReverse(originalStack);

            // 결과 맵에 추가
            result.put(key, transformedStack);
        }
        return result;
    }

    private Stack<String> transformStackRelateReverse(Stack<String> originalStack) {
        Stack<String> resultStack = new Stack<>();
        List<String> tokens = new ArrayList<>(originalStack);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            resultStack.push(token);

            int leftArrowCount = countOccurrences(token, "<");
            int rightArrowCount = countOccurrences(token, ">");
            // 1. 문자열에 '<' 또는 '>'가 두 개 이상 포함된 경우 분해
            if (leftArrowCount == 1) {
                String[] PartsArray = token.split("<");
                resultStack.push(PartsArray[1] + ">" + PartsArray[0]);
            }else if(rightArrowCount == 1){
                String[] PartsArray = token.split(">");
                resultStack.push(PartsArray[1] + "<" + PartsArray[0]);
            }
        }
        System.out.println("[transformStackRelateReverse]" + resultStack);
        return resultStack;

    }

    private Stack<String> transformEngStack(Stack<String> originalStack) {
        Stack<String> resultStack = new Stack<>();
        List<String> tokens = new ArrayList<>(originalStack);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String lowerCaseToken = token.toLowerCase();
            resultStack.push(lowerCaseToken);
        }
        return resultStack;
    }

//    public void setUserDic(String userDic) {
//        try {
//            this.userDic = new Observation();
//            BufferedReader br = new BufferedReader(new FileReader(userDic));
//
//            String line;
//            while((line = br.readLine()) != null) {
//                line = line.trim();
//                if (line.length() != 0 && line.charAt(0) != '#') {
//                    int lastIdx = line.lastIndexOf("\t");
//                    String morph;
//                    String pos;
//                    if (lastIdx == -1) {
//                        morph = line.trim();
//                        pos = "NNP";
//                    } else {
//                        morph = line.substring(0, lastIdx);
//                        pos = line.substring(lastIdx + 1);
//                    }
//
//                    this.userDic.put(morph, pos, this.resources.getTable().getId(pos), 0.0);
//                }
//            }
//
//            br.close();
//            this.userDic.getTrieDictionary().buildFailLink();
//        } catch (Exception var7) {
//            Exception e = var7;
//            e.printStackTrace();
//        }
//    }

    private Stack<String> transformSynonymsStack(Stack<String> originalStack){
        Stack<String> resultStack = new Stack<>();
        List<String> tokens = new ArrayList<>(originalStack);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            String lowerCaseToken = token.toLowerCase();
            resultStack.push(lowerCaseToken);
        }
        return resultStack;
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
    private Stack<String> firstDeepTransformStack(Stack<String> transformedStack) {
        Stack<String> resultStack = new Stack<>();
        List<String> tokens = new ArrayList<>(transformedStack);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);


            int leftArrowCount = countOccurrences(token, "<");
            int rightArrowCount = countOccurrences(token, ">");
            int commaCount = countOccurrences(token, ",");
            // 1. 문자열에 '<' 또는 '>'가 두 개 이상 포함된 경우 분해
            if ((leftArrowCount + rightArrowCount) >= 2 && commaCount == 0) {
                String[] splitParts = splitAtLastSymbol(token,leftArrowCount,rightArrowCount);
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
                    resultStack.push(splitParts[0]);
                    if(splitParts.length != 1){
                        resultStack.push(splitParts[1]); // 분해된 부분
                    }
                }
                resultStack.push(token); // "없" 또는 "있" 추가
                continue;
            }

            // 기본적으로 토큰을 추가
            resultStack.push(token);
        }

        System.out.println("[firstDeepTransformStack]" + resultStack);
        return resultStack;
    }

    private Stack<String> secondDeepTransformStack(Stack<String> transformedStack){
        Stack<String> resultStack = new Stack<>();
        List<String> tokens = new ArrayList<>(transformedStack);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            int andCount = countOccurrences(token, "&");
            // 1. 문자열에 '<' 또는 '>'가 두 개 이상 포함된 경우 분해
            if (andCount > 0) {
                List<String> splitParts = splitAtAndSymbol(token);
                for(String str : splitParts)
                    resultStack.push(str);
            }else {
                resultStack.push(token);
            }
        }
        System.out.println("[secondDeepTransformStack]" + resultStack);
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
    private String[] splitAtLastSymbol(String str,int leftArrowCount, int rightArrowCount) {
        String[] result = null;
        if(leftArrowCount == 1 && rightArrowCount == 1)
            result = splitAtLastSymbolOneLeftArrowAndOneRightArrow(str);
        else if(rightArrowCount == 2)
            result = splitAtLastSymbolOnlyTwoSameArrow(str,'>');
        else if(leftArrowCount == 2)
            result = splitAtLastSymbolOnlyTwoSameArrow(str,'<');
        else
            System.out.println("[splitAtLastSymbol] 복합도가 너무 높아 분석 할 수 없습니다.");
        return result;
    }

    private String[] splitAtLastSymbolOneLeftArrowAndOneRightArrow(String str) {
        int lastLt = str.lastIndexOf("<");
        int lastRt = str.lastIndexOf(">");

        if (lastLt == -1 && lastRt == -1) {
            return null;
        }

        if (lastRt > lastLt) {
            // 마지막 기호가 '>'
            int lastLtBeforeGt = str.lastIndexOf("<", lastRt);
            if (lastLtBeforeGt == -1) {
                // '<'가 없으면 '>'에서만 분리
                String firstPart = str.substring(0, lastRt);
                String secondPart = str.substring(lastRt, str.length());
                return new String[]{firstPart, secondPart};
            } else {
                String firstPart = str.substring(0, lastRt);
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

    private String[] splitAtLastSymbolOnlyTwoSameArrow(String str, char arrow) {
        List<Integer> ltIndices = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == arrow) {
                ltIndices.add(i);
            }
        }
        // '<' 기호가 최소 두 개 있어야 분리 가능
        if (ltIndices.size() < 2) {
            throw new IllegalArgumentException("입력 문자열에 " + arrow + " 기호가 두 개 이상 필요합니다.");
        }

        int firstLtIndex = ltIndices.get(0);
        int secondLtIndex = ltIndices.get(1);
        int wordStart = firstLtIndex + 1;

        // '나는' 단어를 포함한 첫 번째 부분
        String firstPart = str.substring(0, secondLtIndex);
        // '나는' 단어를 포함한 두 번째 부분
        String secondPart = str.substring(wordStart, str.length());

        return new String[]{firstPart, secondPart};
    }

    private List<String> splitAtAndSymbol(String str) {
        List<String> result = new ArrayList<>();
        int leftArrowCount = 0;
        int rightArrowCount = 0;
        leftArrowCount = countOccurrences(str, "<");
        rightArrowCount = countOccurrences(str, ">");
        if(leftArrowCount + rightArrowCount == 0){
            String[] partsArray = str.split("&");
            result = Arrays.asList(partsArray);
        }else if(leftArrowCount == 1 && rightArrowCount == 0){
            String[] basedOnArrowsPartsArray = str.split("<");
            String[] basedOnAndsLeftPartsArray = basedOnArrowsPartsArray[0].split("&");
            String[] basedOnAndsRightPartsArray = basedOnArrowsPartsArray[1].split("&");
            for (String leftString : basedOnAndsLeftPartsArray) {
                for (String rightString : basedOnAndsRightPartsArray) {
                    result.add(leftString+"<"+rightString);
                }
            }
        } else if (leftArrowCount == 0 && rightArrowCount == 1) {
            String[] basedOnArrowsPartsArray = str.split(">");
            String[] basedOnAndsLeftPartsArray = basedOnArrowsPartsArray[0].split("&");
            String[] basedOnAndsRightPartsArray = basedOnArrowsPartsArray[1].split("&");
            for (String leftString : basedOnAndsLeftPartsArray) {
                for (String rightString : basedOnAndsRightPartsArray) {
                    result.add(leftString+">"+rightString);
                }
            }
        }
        return result;
    }

    /**
     * "없"또는 "있" 인 경우 이전 문자열을 분해합니다.
     * 예: "상태>변경" → ["상태>변경", "변경"]
     *
     * @param str 이전 문자열
     * @return 분리된 두 부분의 배열
     */
    private String[] splitAtLastSymbolForEmpty(String str) {
        // 예: "상태>변경" → "상태>변경", "변경"
        int lastGt = str.lastIndexOf(">");
        int lastLt = str.lastIndexOf("<");

        if (lastLt == -1 && lastGt == -1) {
            return new String[]{str};
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
                }
            }
            if(!dbAnswerStack.isEmpty()){
                System.out.println("[matchRateScoring] 미흡한 설명이 존재합니다.");
                return false;
            }
        }
        return true;
    }

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
}
