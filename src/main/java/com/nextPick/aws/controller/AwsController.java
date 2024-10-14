package com.nextPick.aws.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AwsController {

    @GetMapping("/health")
    public ResponseEntity getHealth() {
        return new ResponseEntity(HttpStatus.OK);
    }
}
