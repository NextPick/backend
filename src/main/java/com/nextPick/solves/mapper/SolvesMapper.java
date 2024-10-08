package com.nextPick.solves.mapper;

import com.nextPick.questionList.dto.QuestionListDto;
import com.nextPick.questionList.entity.QuestionList;
import com.nextPick.solves.dto.SolvesDto;
import com.nextPick.solves.entity.Solves;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SolvesMapper {
    default SolvesDto.Response solvesListToSolvesDtoResponse(Solves solves){
        return SolvesDto.Response
                .builder()
                .solvesId(solves.getSolvesId())
                .question(solves.getQuestionList().getQuestion())
                .answer(solves.getQuestionList().getAnswer())
                .myAnswer(solves.getMyAnswer())
                .modifiedAt(solves.getModifiedAt().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                .correct(solves.isCorrect())
                .correctRate(solves.getQuestionList().getCorrectRate())
                .questionCategoryId(solves.getQuestionList().getQuestionListId())
                .build();
    }
    default List<SolvesDto.Responses> solvesListToSolvesDtoResponses(List<Solves> solvesList){
        return solvesList
                .stream()
                .map(solves -> SolvesDto.Responses
                        .builder()
                        .solvesId(solves.getSolvesId())
                        .question(solves.getQuestionList().getQuestion())
                        .correct(solves.isCorrect())
                        .modifiedAt(solves.getModifiedAt().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                        .createdAt(solves.getCreatedAt().format(DateTimeFormatter.ofPattern("MM월 dd일")))
                        .correctRate(solves.getQuestionList().getCorrectRate())
                        .questionCategoryId(solves.getQuestionList().getQuestionListId())
                        .build())
                .collect(Collectors.toList());
    }
}
