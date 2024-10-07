package com.nextPick.questionList.controller;

import com.nextPick.questionList.dto.QuestionListDto;
import com.nextPick.questionList.service.OldQuestionListService;
import com.nextPick.questionList.service.QuestionListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionListController {
    private final OldQuestionListService oldService;
    private final QuestionListService service;

    @PostMapping
    public ResponseEntity komoranTest(@Valid @RequestBody QuestionListDto.answerCheck answerCheck) {
        String answer = answerCheck.getAnswer();
//        service.komoranTestService(answer);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/old/{question-id}")
    public ResponseEntity komoranTest2(@PathVariable("question-id") @Positive long questionId,
            @Valid @RequestBody QuestionListDto.answerCheck answerCheck) {
        boolean result = oldService.judgementResponse(questionId, answerCheck.getAnswer());
        return result?new ResponseEntity<>(HttpStatus.OK):new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/{question-id}")
    public ResponseEntity interviewer(@PathVariable("question-id") @Positive long questionId,
                                       @Valid @RequestBody QuestionListDto.answerCheck answerCheck) {
        boolean result = oldService.judgementResponse(questionId, answerCheck.getAnswer());
        return result?new ResponseEntity<>(HttpStatus.OK):new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
