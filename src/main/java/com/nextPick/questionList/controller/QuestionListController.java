package com.nextPick.questionList.controller;

import com.nextPick.questionList.dto.QuestionListDto;
import com.nextPick.questionList.service.QuestionListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionListController {
    private final QuestionListService service;

    @PostMapping
    public ResponseEntity komoranTest(@Valid @RequestBody QuestionListDto.answerCheck answerCheck) {
        String answer = answerCheck.getAnswer();
        service.komoranTestService(answer);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
