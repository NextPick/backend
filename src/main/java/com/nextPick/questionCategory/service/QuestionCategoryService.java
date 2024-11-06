package com.nextPick.questionCategory.service;

import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.questionCategory.repository.QuestionCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionCategoryService {
    private final QuestionCategoryRepository repository;

    public List<QuestionCategory> getQuestionCategoryList(){
        return repository.findAll();
    }
}
