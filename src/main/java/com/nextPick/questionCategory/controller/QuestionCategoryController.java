package com.nextPick.questionCategory.controller;

import com.nextPick.dto.SingleResponseDto;
import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.questionCategory.mapper.QuestionCategoryMapper;
import com.nextPick.questionCategory.service.QuestionCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequestMapping("/question/category")
@RequiredArgsConstructor
public class QuestionCategoryController {
    private final QuestionCategoryService service;
    private final QuestionCategoryMapper mapper;

    @GetMapping
    public ResponseEntity getQuestionCategoryList() {
        List<QuestionCategory> questionCategoryList = service.getQuestionCategoryList();
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.questionCategoryListToQuestionCategoryDtoResponse(questionCategoryList)), HttpStatus.OK);
    }
}
