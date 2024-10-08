package com.nextPick.websocket.dto;

import lombok.Data;

@Data
public class EnterRoomReq {
    private String roomId;
    private String camKey;
}
