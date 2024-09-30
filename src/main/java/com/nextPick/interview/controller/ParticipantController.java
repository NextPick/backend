package com.nextPick.interview.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/participants")
@RequiredArgsConstructor
public class ParticipantController {
    private final static String PARTICIPANT_DEFAULT_URL = "/participants";
}
