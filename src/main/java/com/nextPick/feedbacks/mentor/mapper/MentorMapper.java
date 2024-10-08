package com.nextPick.feedbacks.mentor.mapper;

import com.nextPick.feedbacks.mentor.dto.MentorDto;
import com.nextPick.feedbacks.mentor.entity.Mentor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MentorMapper {
    Mentor mentorPostDtoToMentor(MentorDto.Post post);

    List<MentorDto.Responses> mentorListToMentorResponsesDto(List<Mentor> mentorList);
}
