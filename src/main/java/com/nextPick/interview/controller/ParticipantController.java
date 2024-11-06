package com.nextPick.interview.controller;

import com.nextPick.dto.SingleResponseDto;
import com.nextPick.interview.dto.ParticipantDto;
import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.entity.Room;
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

//    @PostMapping
//    public ResponseEntity postParticipant(@PathVariable("uuid") String uuid) {
//        Participant createParticipant = service.createParticipant(uuid);
//
//        String PARTICIPANT_URL = "/rooms/" + uuid + "/participants";
//        URI location = UriCreator.createUri(PARTICIPANT_URL, createParticipant.getParticipantId());
//
//        return ResponseEntity.created(location).build();
//    }

    @GetMapping("/{camKey}")
    public ResponseEntity getParticipants(@PathVariable("uuid") String uuid,
                                          @PathVariable("camKey") String camKey) {
        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.participantToParticipantDto(
                        service.findParticipantByRoomUuidAndCamKey(uuid, camKey))), HttpStatus.OK);
    }

//    @DeleteMapping
//    public ResponseEntity deleteParticipant(@PathVariable("uuid") String uuid) {
//        service.deleteParticipant(uuid);
//
//        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//    }
}
