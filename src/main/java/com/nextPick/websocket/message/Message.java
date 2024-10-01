package com.nextPick.websocket.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String sender;
    private String type;
    private String roomId;
    private String data;
    private Object allUsers;
    private String receiver;
    private Object answer;
    private Object candidate;
    private Object sdp;
}
