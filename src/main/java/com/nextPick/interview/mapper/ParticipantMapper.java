package com.nextPick.interview.mapper;

import com.nextPick.interview.dto.ParticipantDto;
import com.nextPick.interview.entity.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    @Mapping(source = "uuid", target = "room.uuid")
    @Mapping(source = "memberId", target = "member.memberId")
    Participant participantDtoPostToParticipant(ParticipantDto.Post requestBody);
}
