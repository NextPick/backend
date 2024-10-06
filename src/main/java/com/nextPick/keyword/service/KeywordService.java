package com.nextPick.keyword.service;

import com.nextPick.keyword.entity.Keyword;
import com.nextPick.keyword.repository.KeywordRepository;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService extends ExtractMemberAndVerify {
    private final KeywordRepository keywordRepository;

    public void createKeyword (List<String> word, List<String> wordExplain, QuestionList questionList) {
        for (int i = 0; i < word.size(); i++) {
            Keyword keyword = new Keyword();
            keyword.setQuestionList(questionList);
            keyword.setWord(word.get(i));
            if (!wordExplain.get(i).isEmpty())
                keyword.setWordExplain(wordExplain.get(i));
            keywordRepository.save(keyword);
        }
    }

    public void updateKeyword (List<String> word, List<String> wordExplain, QuestionList questionList) {
        keywordRepository.deleteAllByQuestionList(questionList);
        createKeyword(word, wordExplain, questionList);
    }

    public void deleteKeyword(QuestionList questionList) {
        keywordRepository.deleteAllByQuestionList(questionList);
    }
}
