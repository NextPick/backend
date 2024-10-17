package com.nextPick.questionList.mapper;

import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.entity.Member;
import com.nextPick.questionCategory.entity.QuestionCategory;
import com.nextPick.questionList.dto.QuestionListDto;
import com.nextPick.questionList.entity.QuestionList;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionListMapper {
    QuestionList questionListPostToQuestionList(QuestionListDto.Post post);
    QuestionList questionListPatchToQuestionList(QuestionListDto.Patch post);
    QuestionListDto.Response questionListToQuestionListDtoResponse(QuestionList questionList);

    default QuestionListDto.ResponseSolvesId LongToQuestionListDtoResponseSolvesId(Long id,Long booleanValue) {
        QuestionListDto.ResponseSolvesId result = new QuestionListDto.ResponseSolvesId();
        result.setSolvesId(id);
        if (booleanValue == 1L)
            result.setResult(true);
        else
            result.setResult(false);
        return result;
    }

    default List<QuestionListDto.ResponsesTypeNone> questionListsToQuestionListDtoResponseTypeNone(List<QuestionList> questionLists){
        return questionLists
                .stream()
                .map(questionList -> QuestionListDto.ResponsesTypeNone
                        .builder()
                        .questionListId(questionList.getQuestionListId())
                        .question(questionList.getQuestion())
                        .correctRate(questionList.getCorrectRate())
                        .questionCategory(questionList.getQuestionCategory())
                        .build())
                .collect(Collectors.toList());
    }

    default List<QuestionListDto.ResponsesTypeRandom> questionListsToQuestionListDtoResponseTypeRandom(List<QuestionList> questionLists){
        return questionLists
                .stream()
                .map(questionList -> QuestionListDto.ResponsesTypeRandom
                        .builder()
                        .questionListId(questionList.getQuestionListId())
                        .question(questionList.getQuestion())
                        .answer(questionList.getAnswer())
                        .correctRate(questionList.getCorrectRate())
                        .questionCategory(questionList.getQuestionCategory())
                        .build())
                .collect(Collectors.toList());
    }
}
