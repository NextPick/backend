package com.nextPick.interview.controller;

import com.nextPick.interview.service.RoomService;
import com.nextPick.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final static String ROOM_DEFAULT_URL = "/rooms";
    private final RoomService roomService;

//    @PostMapping
//    public ResponseEntity postRoom() {
//        URI location = UriCreator.createUri(ROOM_DEFAULT_URL);
//    }
}
