package com.nextPick.interview.mapper;

import com.nextPick.interview.dto.ParticipantDto;
import com.nextPick.interview.entity.Participant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    @Mapping(source = "uuid", target = "room.uuid")
    Participant participantDtoPostToParticipant(ParticipantDto.Post requestBody);
    @Mapping(source = "member.nickname", target = "nickname")
    @Mapping(source = "member.occupation", target = "occupation")
    ParticipantDto.Response participantToParticipantDto(Participant participant);
    List<ParticipantDto.Response> participantListToParticipantDtoList(List<Participant> participantList);
}
