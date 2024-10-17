package com.nextPick.websocket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class SignalingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket 메시지 핸들러: Simple Peer에서 오퍼를 수신하고 응답을 전송합니다.
     *
     * @param offer  수신한 오퍼 메시지
     * @param roomId 방 ID
     * @param camId  카메라 ID
     * @return 오퍼 메시지
     */
    @MessageMapping("/simple-peer/offer/{camId}/{roomId}")
    @SendTo("/topic/simple-peer/answer/{camId}/{roomId}")
    public String simplePeerHandleOffer(@Payload String offer,
                                        @DestinationVariable(value = "roomId") String roomId,
                                        @DestinationVariable(value = "camId") String camId) {
        return offer;
    }

    /**
     * WebSocket 메시지 핸들러: Simple Peer에서 ICE 후보를 수신하고 응답을 전송합니다.
     *
     * @param candidate 수신한 ICE 후보 메시지
     * @param roomId    방 ID
     * @return ICE 후보 메시지
     */
    @MessageMapping("/simple-peer/iceCandidate/{roomId}")
    @SendTo("/topic/simple-peer/iceCandidate/{roomId}")
    public String SimplePeerHandleIceCandidate(@Payload String candidate, @DestinationVariable String roomId) {
        return candidate;
    }

    /**
     * WebSocket 메시지 핸들러: 카메라 ID를 요청하고 응답을 전송합니다.
     *
     * @param body   요청 본문
     * @param roomId 방 ID
     * @return 요청 본문
     */
    @MessageMapping("/simple-peer/cam/getCamId/{roomId}")
    @SendTo("/topic/simple-peer/cam/getCamId/{roomId}")
    public String SimplePeerCamGetCamId(@Payload String body, @DestinationVariable String roomId) {
        return body;
    }

    /**
     * WebSocket 메시지 핸들러: 스트리밍 카메라 ID를 요청하고 응답을 전송합니다.
     *
     * @param body   요청 본문
     * @param roomId 방 ID
     * @return 요청 본문
     */
    @MessageMapping("/simple-peer/stream/getCamId/{roomId}")
    @SendTo("/topic/simple-peer/stream/getCamId/{roomId}")
    public String SimplePeerStreamGetCamId(@Payload String body, @DestinationVariable String roomId) {
        return body;
    }

    /**
     * WebSocket 메시지 핸들러: Peer에서 오퍼를 수신하고 응답을 전송합니다.
     *
     * @param offer   수신한 오퍼 메시지
     * @param roomId  방 ID
     * @param camKey  카메라 키
     * @return 오퍼 메시지
     */
    @MessageMapping("/peer/offer/{camKey}/{roomId}")
    @SendTo("/topic/peer/offer/{camKey}/{roomId}")
    public String PeerHandleOffer(@Payload String offer, @DestinationVariable(value = "roomId") String roomId,
                                  @DestinationVariable(value = "camKey") String camKey) {
        log.info("[OFFER] {} : {}", camKey, offer);
        return offer;
    }

    /**
     * WebSocket 메시지 핸들러: Peer에서 ICE 후보를 수신하고 응답을 전송합니다.
     *
     * @param candidate 수신한 ICE 후보 메시지
     * @param roomId    방 ID
     * @param camKey    카메라 키
     * @return ICE 후보 메시지
     */
    @MessageMapping("/peer/iceCandidate/{camKey}/{roomId}")
    @SendTo("/topic/peer/iceCandidate/{camKey}/{roomId}")
    public String PeerHandleIceCandidate(@Payload String candidate, @DestinationVariable(value = "roomId") String roomId,
                                         @DestinationVariable(value = "camKey") String camKey) {
        log.info("[ICECANDIDATE] {} : {}", camKey, candidate);
        return candidate;
    }

    /**
     * WebSocket 메시지 핸들러: Peer에서 응답을 수신하고 응답을 전송합니다.
     *
     * @param answer  수신한 응답 메시지
     * @param roomId  방 ID
     * @param camKey  카메라 키
     * @return 응답 메시지
     */
    @MessageMapping("/peer/answer/{camKey}/{roomId}")
    @SendTo("/topic/peer/answer/{camKey}/{roomId}")
    public String PeerHandleAnswer(@Payload String answer, @DestinationVariable(value = "roomId") String roomId,
                                   @DestinationVariable(value = "camKey") String camKey) {
        log.info("[ANSWER] {} : {}", camKey, answer);
        return answer;
    }

    /**
     * WebSocket 메시지 핸들러: 키 요청을 수신하고 응답을 전송합니다.
     *
     * @param message 수신한 키 메시지
     * @return 키 메시지
     */
    @MessageMapping("/call/key")
    @SendTo("/topic/call/key")
    public String callKey(@Payload String message) {
        log.info("[Key] : {}", message);
        return message;
    }

    /**
     * WebSocket 메시지 핸들러: 키를 전송하고 응답을 전송합니다.
     *
     * @param message 수신한 키 메시지
     * @return 키 메시지
     */
    @MessageMapping("/send/key")
    @SendTo("/topic/send/key")
    public String sendKey(@Payload String message) {
        return message;
    }

    /**
     * WebSocket 메시지 핸들러: 스트림 시작 요청을 수신하고 응답을 전송합니다.
     *
     * @param message 수신한 메시지
     * @return 메시지
     */
    @MessageMapping("/peer/start/steam")
    @SendTo("/topic/peer/start/steam")
    public String peerStartSteam(@Payload String message) {
        return message;
    }

    public void someMethodWhereYouSendMessages(String camKey, String roomUuid, String memberType, Long memberId) {
        messagingTemplate.convertAndSend("/topic/roomUuid/" + camKey, roomUuid);
        messagingTemplate.convertAndSend("/topic/memberType/" + camKey, memberType);
        messagingTemplate.convertAndSend("/topic/memberId/" + camKey, memberId);
    }
}
