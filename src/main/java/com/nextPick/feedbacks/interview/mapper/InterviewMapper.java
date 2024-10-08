package com.nextPick.feedbacks.interview.mapper;

import com.nextPick.feedbacks.interview.dto.InterviewDto;
import com.nextPick.feedbacks.interview.entity.Interview;
import com.nextPick.feedbacks.mentor.dto.MentorDto;
import com.nextPick.report.dto.ReportDto;
import com.nextPick.report.entity.Report;
import org.mapstruct.Mapper;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    Interview interviewPostDtoToInterview(InterviewDto.Post post);

    default List<InterviewDto.Responses> interviewListToInterviewResponsesDto(List<Interview> interviewList){
        return interviewList
                .stream()
                .map(interview -> InterviewDto.Responses
                        .builder()
                        .content(interview.getContent())
                        .createdAt(interview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build())
                .collect(Collectors.toList());
    }

    default List<InterviewDto.ResponsesForAdmin> interviewListToInterviewResponsesDtoForAdmin(List<Interview> interviewList){
        return interviewList
                .stream()
                .map(interview -> InterviewDto.ResponsesForAdmin
                        .builder()
                        .mentor(interview.getMentor().getName())
                        .content(interview.getContent())
                        .createdAt(interview.getModifiedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                        .build())
                .collect(Collectors.toList());
    }
}
