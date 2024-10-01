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
        String strToAnalyze = "Persist는 JPA에서 엔티티를 영속성 컨텍스트에 저장하는 행위를 의미합니다. 이는 데이터베이스에 해당 엔티티가 저장된다는 것을 보장하며, 영속성 컨텍스트에서 관리되기 시작합니다.";
        String strToAnalyze2 = "Persist는 JPA에서 영속성 컨텍스트에 엔티티를 저장하는 행위입니다. 영속성 컨텍스트에서 관리되며, 데이터베이스에 엔티티가 저장된다는 것을 보장합니다.";
        String strToAnalyze3 = "싱글톤 패턴은 전역 상태를 공유하기 때문에, 테스트하기 어렵고 의존성이 강해질 수 있습니다. 또한, 멀티스레드 환경에서는 동기화를 신경 써야 합니다.";
        List<String> keywords = new ArrayList<>();
        keywords.add("A와 E가 B로 느리게 저장되고 C으로 D가 빠르게 저장됨");
        keywords.add("엔티티를 영속성 컨텍스트에 저장");
        keywords.add("엔티티가 데이터베이스에 저장되는 것을 보장");

        List<String> NNRelated = new ArrayList<>();

        List<String> splitText2 = new ArrayList<>();
        for(String keyword : keywords){
            StringBuilder translation = new StringBuilder();
            KomoranResult analyzeResultList = komoran.analyze(keyword);
            List<Token> tokenList = analyzeResultList.getTokenList();
            boolean related = false;
            boolean NNDouble = false;
            for (int i = 0; i < tokenList.size(); i++){{
                Token token = tokenList.get(i);
                // 분석해야 하는 형태소라면은..?
                switch (token.getPos()){
                    case "JKS": // 가
                    case "JKO": // 를, 은, 는
                        if(!related) {
                            translation.append(">");
                            NNDouble = false;
                        }
                        else
                            translation.append("/");
                        related = !related;
                        break;
                    case "JC":  // 와
                        translation.append("&");
                        break;
                    case "JKB": // 에서, 에, 으로
                        if(!related) {
                            translation.append("<");
                            NNDouble = false;
                        }
                        else
                            translation.append("/");
                        related = !related;
                        break;
                    case "VV":  // 동사
                    case "VA":  // 형용사
                    case "NNG": // 일반 명사
                    case "NNP": // 고유 명사
                    case "SL":  // 외국어
                        if(NNDouble){
                            translation.append("/");
                            NNDouble = false;
                        }else{
                            NNDouble = true;
                        }
                        translation.append(token.getMorph());
                        if(related) {
                            splitText2.add(String.valueOf(translation));
                            translation.append("/");
                        }
                        break;
                }
//                System.out.format("[keywords] %s/%s\n", token.getMorph(), token.getPos());
            }
//                System.out.format("[keywords] %s/%s\n", token.getMorph(), token.getPos());
            }
            System.out.format("[keywords-translation] %s\n", translation);
            System.out.println(keyword);
        }

        List<String> splitText = new ArrayList<>();
        List<String> trimAnswerText = new ArrayList<>(Arrays.asList(strToAnalyze3.split("[.?!]")));
        for(String answerTextSingle : trimAnswerText){
            StringBuilder translation = new StringBuilder();
            KomoranResult analyzeResultList = komoran.analyze(answerTextSingle);
            List<Token> tokenList = analyzeResultList.getTokenList();
            boolean related = false;
            boolean NNDouble = false;
            for (int i = 0; i < tokenList.size(); i++){
                Token token = tokenList.get(i);
                // 분석해야 하는 형태소라면은..?
                switch (token.getPos()){
                    case "JKS": // 가
                    case "JKO": // 를, 은, 는
                        if(!related) {
                            translation.append(">");
                            NNDouble = false;
                        }
                        else
                            translation.append("/");
                        related = !related;
                        break;
                    case "JC":  // 와
                        translation.append("&");
                        break;
                    case "JKB": // 에서, 에, 으로
                        if(!related) {
                            translation.append("<");
                            NNDouble = false;
                        }
                        else
                            translation.append("/");
                        related = !related;
                        break;
                    case "VV":  // 동사
                    case "VA":  // 형용사
                    case "NNG": // 일반 명사
                    case "NNP": // 고유 명사
                    case "SL":  // 외국어
                        if(NNDouble){
                            translation.append("/");
                            NNDouble = false;
                        }else{
                            NNDouble = true;
                        }
                        translation.append(token.getMorph());
                        if(related) {
                            splitText.add(String.valueOf(translation));
                            translation.append("/");
                        }
                        break;
                }
//                System.out.format("[keywords] %s/%s\n", token.getMorph(), token.getPos());
            }
            System.out.format("[keywords-translation] %s\n", translation);
            for(String str : splitText){
                System.out.println("[keywords-split]" + str);
            }
            System.out.println(answerTextSingle);
        }
    }
}
