package com.nextPick.websocket.handler;

import com.nextPick.websocket.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SignalingHandler extends TextWebSocketHandler {

    private final Map<String, List<Map<String, String>>> roomInfo = new HashMap<>();
    private final Map<String, String> userInfo = new HashMap<>();
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final int MAXIMUM = 8;

    private static final String MSG_TYPE_OFFER = "offer";
    private static final String MSG_TYPE_ANSWER = "answer";
    private static final String MSG_TYPE_CANDIDATE = "candidate";
    private static final String MSG_TYPE_JOIN = "join_room";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info(">>> [ws] 클라이언트 접속 : 세션 - {}", session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        try {
            Message message = Utils.getObject(textMessage.getPayload());
            log.info(">>> [ws] 시작!!! 세션 객체 {}", session);

            String userUUID = session.getId();
            String roomId = message.getRoom();
            log.info(">>> [ws] 메시지 타입 {}, 보낸 사람 {}", message.getType(), userUUID);

            switch (message.getType()) {
                case MSG_TYPE_OFFER:
                case MSG_TYPE_ANSWER:
                case MSG_TYPE_CANDIDATE:
                    Object candidate = message.getCandidate();
                    Object sdp = message.getSdp();
                    String receiver = message.getReceiver();
                    log.info(">>> [ws] receiver {}", receiver);
                    sessions.values().forEach(s -> {
                        try {
                            if (s.getId().equals(receiver)) {
                                s.sendMessage(new TextMessage(Utils.getString(Message.builder()
                                        .type(message.getType())
                                        .sdp(sdp)
                                        .candidate(candidate)
                                        .sender(userUUID)
                                        .receiver(receiver).build())));
                            }
                        } catch (Exception e) {
                            log.info(">>> 에러 발생 : offer, candidate, answer 메시지 전달 실패 {}", e.getMessage());
                        }
                    });
                    break;

                case MSG_TYPE_JOIN:
                    log.info(">>> [ws] {} 가 #{}번 방에 들어감", userUUID, roomId);
                    if (roomInfo.containsKey(roomId)) {
                        int currentRoomLength = roomInfo.get(roomId).size();
                        if (currentRoomLength == MAXIMUM) {
                            session.sendMessage(new TextMessage(Utils.getString(Message.builder()
                                    .type("room_full")
                                    .sender(userUUID).build())));
                            return;
                        }
                        Map<String, String> userDetail = new HashMap<>();
                        userDetail.put("id", userUUID);
                        roomInfo.get(roomId).add(userDetail);
                        log.info(">>> [ws] #{}번 방의 유저들 {}", roomId, roomInfo.get(roomId));
                    } else {
                        Map<String, String> userDetail = new HashMap<>();
                        userDetail.put("id", userUUID);
                        List<Map<String, String>> newRoom = new ArrayList<>();
                        newRoom.add(userDetail);
                        roomInfo.put(roomId, newRoom);
                    }
                    sessions.put(userUUID, session);
                    userInfo.put(userUUID, roomId);
                    List<Map<String, String>> originRoomUser = new ArrayList<>();
                    for (Map<String, String> userDetail : roomInfo.get(roomId)) {
                        if (!(userDetail.get("id").equals(userUUID))) {
                            Map<String, String> userMap = new HashMap<>();
                            userMap.put("id", userDetail.get("id"));
                            originRoomUser.add(userMap);
                        }
                    }
                    log.info(">>> [ws] 본인 {} 을 제외한 #{}번 방의 다른 유저들 {}", userUUID, roomId, originRoomUser);
                    session.sendMessage(new TextMessage(Utils.getString(Message.builder()
                            .type("all_users")
                            .allUsers(originRoomUser)
                            .sender(userUUID).build())));
                    break;

                default:
                    log.info(">>> [ws] 잘못된 메시지 타입 {}", message.getType());
            }
        } catch (IOException e) {
            log.info(">>> 에러 발생 : 양방향 데이터 통신 실패 {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info(">>> [ws] 클라이언트 접속 해제 : 세션 - {}, 상태 - {}", session, status);
        String userUUID = session.getId();
        String roomId = userInfo.get(userUUID);

        sessions.remove(userUUID);
        userInfo.remove(userUUID);

        List<Map<String, String>> removed = new ArrayList<>();
        roomInfo.get(roomId).forEach(s -> {
            try {
                if (s.containsValue(userUUID)) {
                    removed.add(s);
                }
            } catch (Exception e) {
                log.info(">>> 에러 발생 : if문 생성 실패 {}", e.getMessage());
            }
        });
        roomInfo.get(roomId).removeAll(removed);

        sessions.values().forEach(s -> {
            try {
                if (!(s.getId().equals(userUUID))) {
                    s.sendMessage(new TextMessage(Utils.getString(Message.builder()
                            .type("user_exit")
                            .sender(userUUID).build())));
                }
            } catch (Exception e) {
                log.info(">>> 에러 발생 : user_exit 메시지 전달 실패 {}", e.getMessage());
            }
        });

        log.info(">>> [ws] #{}번 방에서 {} 삭제 완료", roomId, userUUID);
        log.info(">>> [ws] #{}번 방에 남은 유저 {}", roomId, roomInfo.get(roomId));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.info(">>> 에러 발생 : 소켓 통신 에러 {}", exception.getMessage());
    }
}

