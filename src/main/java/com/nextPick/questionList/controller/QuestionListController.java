package com.nextPick.questionList.controller;

import com.nextPick.dto.MultiResponseDto;
import com.nextPick.dto.SingleResponseDto;
import com.nextPick.member.entity.Member;
import com.nextPick.questionList.dto.QuestionListDto;
//import com.nextPick.questionList.service.OldQuestionListService;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.questionList.mapper.QuestionListMapper;
import com.nextPick.questionList.service.QuestionListService;
import com.nextPick.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionListController {
    private final static String QUESTION_DEFAULT_URL = "/questions";
//    private final OldQuestionListService oldService;
    private final QuestionListService service;
    private final QuestionListMapper mapper;

    @PostMapping("/admin")
    public ResponseEntity createQuestionList(@Valid @RequestBody QuestionListDto.Post requestBody) {
        QuestionList questionList = mapper.questionListPostToQuestionList(requestBody);
        service.createQuestionList(questionList,requestBody.getQuestionCategoryId());
        URI location = UriCreator.createUri(QUESTION_DEFAULT_URL, questionList.getQuestionListId());
        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/admin/{question-id}")
    public ResponseEntity updateQuestionList(@PathVariable("question-id") @Positive long questionId,
                                             @Valid @RequestBody QuestionListDto.Patch requestBody) {
        QuestionList questionList = service.updateQuestionList(mapper.questionListPatchToQuestionList(requestBody),questionId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.questionListToQuestionListDtoResponse(questionList)), HttpStatus.OK);
    }

    @DeleteMapping("/admin/{question-id}")
    public ResponseEntity deleteQuestionList(@PathVariable("question-id") @Positive long questionId) {
        service.deleteQuestionList(questionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{question-id}")
    public ResponseEntity getQuestionList(@PathVariable("question-id") @Positive long questionId) {
        QuestionList questionList = service.findQuestionList(questionId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.questionListToQuestionListDtoResponse(questionList)), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity getQuestionLists(@Positive @RequestParam int page,
                                           @Positive @RequestParam int size,
                                           @RequestParam(name = "category") long questionCategoryId,
                                           @RequestParam(name = "keyword") String keyword,
                                           @RequestParam(name = "type") String type,
                                           @RequestParam(name = "sort") String sort ){
        if(type.equals("none")) {
            Page<QuestionList> pageQuestionLists =
                    service.findQuestionLists(page - 1, size, questionCategoryId, keyword, sort);
            List<QuestionList> QuestionLists = pageQuestionLists.getContent();
            return new ResponseEntity<>(
                    new MultiResponseDto<>(mapper.questionListsToQuestionListDtoResponseTypeNone(QuestionLists), pageQuestionLists),
                    HttpStatus.OK);
        }else{
            List<QuestionList> QuestionLists = service.findQuestionLists(size,questionCategoryId);
            return new ResponseEntity<>(
                    new SingleResponseDto<>(mapper.questionListsToQuestionListDtoResponseTypeRandom(QuestionLists)),
                    HttpStatus.OK);
        }
    }

//    @PostMapping("/old/{question-id}")
//    public ResponseEntity komoranTest2(@PathVariable("question-id") @Positive long questionId,
//            @Valid @RequestBody QuestionListDto.answerCheck answerCheck) {
//        boolean result = oldService.judgementResponse(questionId, answerCheck.getAnswer());
//        return result?new ResponseEntity<>(HttpStatus.OK):new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }

    @PostMapping("/{question-id}/score")
    public ResponseEntity interviewer(@PathVariable("question-id") @Positive long questionId,
                                       @Valid @RequestBody QuestionListDto.answerCheck answerCheck) {
        boolean result = service.scoringInterview(questionId, answerCheck.getAnswer());
        return result?new ResponseEntity<>(HttpStatus.OK):new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
