package com.nextPick.feedbacks.mentor.mapper;

import com.nextPick.feedbacks.mentor.dto.MentorDto;
import com.nextPick.feedbacks.mentor.entity.Mentor;
import com.nextPick.questionList.dto.QuestionListDto;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MentorMapper {
    Mentor mentorPostDtoToMentor(MentorDto.Post post);

    default List<MentorDto.Responses> mentorListToMentorResponsesDto(List<Mentor> mentorList){
        return mentorList
                .stream()
                .map(mentor -> MentorDto.Responses
                        .builder()
                        .content(mentor.getContent())
                        .createdAt(mentor.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .startRating(mentor.getStarRating())
                        .build())
                .collect(Collectors.toList());
    }

    default List<MentorDto.ResponsesForAdmin> mentorListToMentorResponsesDtoForAdmin(List<Mentor> mentorList){
        return mentorList
                .stream()
                .map(mentor -> MentorDto.ResponsesForAdmin
                        .builder()
                        .name((mentor.getMentee().getName()))
                        .content(mentor.getContent())
                        .createdAt(mentor.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .startRating(mentor.getStarRating())
                        .build())
                .collect(Collectors.toList());
    }
}
