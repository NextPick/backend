package com.nextPick.questionCategory.mapper;

import com.nextPick.questionCategory.dto.QuestionCategoryDto;
import com.nextPick.questionCategory.entity.QuestionCategory;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionCategoryMapper {
    default List<QuestionCategoryDto.Response> questionCategoryListToQuestionCategoryDtoResponse(List<QuestionCategory> questionCategoryList){
        return questionCategoryList
                .stream()
                .map(questionCategory -> QuestionCategoryDto.Response
                        .builder()
                        .questionCategoryId(questionCategory.getQuestionCategoryId())
                        .categoryName(questionCategory.getCategoryName())
                        .build())
                .collect(Collectors.toList());
    }
}
