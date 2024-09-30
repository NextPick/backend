package com.nextPick.interview.controller;

import com.nextPick.interview.dto.ParticipantDto;
import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.mapper.ParticipantMapper;
import com.nextPick.interview.service.ParticipantService;
import com.nextPick.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/rooms/{uuid}/participants")
@RequiredArgsConstructor
public class ParticipantController {
    private final static String PARTICIPANT_DEFAULT_URL = "/rooms/{uuid}/participants";
    private final ParticipantService service;
    private final ParticipantMapper mapper;

    @PostMapping
    public void postParticipant(@PathVariable String uuid,
                                @Valid @RequestBody ParticipantDto.Post requestBody) {
        requestBody.setUuid(uuid);
        Participant participant = mapper.participantDtoPostToParticipant(requestBody);
        Participant createParticipant = service.createParticipant(participant);
    }

    @DeleteMapping
    public ResponseEntity deleteParticipant(@PathVariable String uuid,
                                  @Valid @RequestBody ParticipantDto.Post requestBody) {
        requestBody.setUuid(uuid);
        Participant participant = mapper.participantDtoPostToParticipant(requestBody);
        service.deleteParticipant(participant);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
