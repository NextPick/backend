package com.nextPick.feedbacks.interview.mapper;

import com.nextPick.feedbacks.interview.dto.InterviewDto;
import com.nextPick.feedbacks.interview.entity.Interview;
import com.nextPick.report.dto.ReportDto;
import com.nextPick.report.entity.Report;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InterviewMapper {
    Interview interviewPostDtoToInterview(InterviewDto.Post post);

    List<InterviewDto.Responses> interviewListToInterviewResponsesDto(List<Interview> interviewList);
}
