package com.nextPick.solves.controller;


import com.nextPick.dto.MultiResponseDto;
import com.nextPick.dto.SingleResponseDto;
import com.nextPick.questionList.dto.QuestionListDto;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.solves.dto.SolvesDto;
import com.nextPick.solves.entity.Solves;
import com.nextPick.solves.mapper.SolvesMapper;
import com.nextPick.solves.service.SolvesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.SortedMap;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/solves")
public class SolvesController {
    private final SolvesService service;
    private final SolvesMapper mapper;

    @GetMapping
    public ResponseEntity getQuestionLists(@Positive @RequestParam int page,
                                           @Positive @RequestParam int size,
                                           @RequestParam(name = "category") Long questionCategoryId,
                                           @RequestParam(name = "keyword") String keyword,
                                           @RequestParam(name = "correct") boolean correct,
                                           @RequestParam(name = "sort") String sort ){
        Page<Solves> pageSolvesList =
                service.getSolvesPage(page - 1, size, questionCategoryId, keyword, sort,correct);
        List<Solves> SolvesList = pageSolvesList.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.solvesListToSolvesDtoResponses(SolvesList), pageSolvesList),
                HttpStatus.OK);
    }

    @GetMapping("/{solves-id}")
    public ResponseEntity getSolves(@PathVariable("solves-id") @Positive long solvesId) {
        Solves solves = service.getSolves(solvesId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.solvesListToSolvesDtoResponse(solves)), HttpStatus.OK);
    }

    @PostMapping("/list")
    public ResponseEntity getSolveList(@Valid @RequestBody SolvesDto.getList requestBody) {
        List<Solves> solvesList = service.getSolveList(requestBody.getSolvesIdList());
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.solvesListToSolvesDtoResponsesList(solvesList)), HttpStatus.OK);
    }
}
