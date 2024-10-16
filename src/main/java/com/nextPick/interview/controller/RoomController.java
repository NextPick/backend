package com.nextPick.interview.controller;

import com.nextPick.dto.SingleResponseDto;
import com.nextPick.interview.dto.RoomDto;
import com.nextPick.interview.entity.Room;
import com.nextPick.interview.mapper.RoomMapper;
import com.nextPick.interview.service.RoomService;
import com.nextPick.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final static String ROOM_DEFAULT_URL = "/rooms";
    private final RoomService service;
    private final RoomMapper mapper;

    @PostMapping
    public ResponseEntity postRoom(@Validated @RequestBody RoomDto.Post requestBody) {
        Room room = mapper.roomDtoPostToRoom(requestBody);
        Room createRoom = service.createRoom(room);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomToRoomDtoPostResponse(createRoom)), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getRoomsCount() {
        int roomsCount = service.findRoomsCount();

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomsCountToRoomDtoResponse(roomsCount)), HttpStatus.OK);
    }

    @GetMapping("/{occupation}")
    public ResponseEntity getActiveRoom(@PathVariable("occupation") String occupation) {
        Room room = service.findActiveRoom(occupation);

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomToRoomDtoPostResponse(room)), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity deleteRoom(@PathVariable String uuid) {
        service.deleteRoom(uuid);

        return ResponseEntity.noContent().build();
    }
}
