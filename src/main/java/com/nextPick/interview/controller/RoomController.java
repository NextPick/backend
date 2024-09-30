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
        URI location = UriCreator.createUri(ROOM_DEFAULT_URL,createRoom.getUuid());

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity getRoomsCount() {
        Long roomsCount = service.findRoomsCount();

        return new ResponseEntity<>(
                new SingleResponseDto<>(mapper.roomsCountToRoomDtoResponse(roomsCount)), HttpStatus.OK);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity deleteRoom(@PathVariable String uuid) {
        service.deleteRoom(uuid);

        return ResponseEntity.noContent().build();
    }
}
