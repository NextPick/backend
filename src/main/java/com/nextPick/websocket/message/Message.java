package com.nextPick.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;
    private String type;
    private Long roomId;
    private String data;
    private Object allUsers;
    private String receiver;
    private Object answer;
    private Object candidate;
    private Object sdp;

}
