package com.nextPick.websocket.config;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.interview.entity.Participant;
import com.nextPick.interview.entity.Room;
import com.nextPick.interview.repository.ParticipantRepository;
import com.nextPick.interview.repository.RoomRepository;
import com.nextPick.interview.service.ParticipantService;
import com.nextPick.interview.service.RoomService;
import com.nextPick.member.entity.Member;
import com.nextPick.member.service.MemberService;
import com.nextPick.utils.ExtractMemberAndVerify;
import com.nextPick.utils.JwtUtil;
import com.nextPick.websocket.dto.CommonResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class WebSocketEventListener extends ExtractMemberAndVerify {
    @Autowired
    private GlobalVariables globalVariables;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @EventListener
    public void handleWebsocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Map<String, List<String>> nativeHeaders = getNativeHeaders(event);

        String camKey = nativeHeaders.get("camKey").get(0);
        String occupation = nativeHeaders.get("occupation").get(0);
        String email = nativeHeaders.get("email").get(0);

        Member member = memberService.findMemberByEmail(email);

        // 직군으로 분류하여 사람이 꽉 차있지 않은 방을 찾기
        Room room = roomService.findActiveRoom(occupation);
        String roomUUid = room.getUuid();

        // 해당 세션 Id에 대한 룸 Id 가 있는지 확인
        if(room.getSessionId() == null) {
            //없다면 추가 해준다.
            room.setSessionId(sessionId);
        }

        participantService.createParticipant(room, member, sessionId, camKey);

        room = roomRepository.save(room);

        int participantCount = participantService.findParticipantCount(room.getUuid());

        messagingTemplate.convertAndSend("/topic/roomId/" + sessionId, room.getRoomId());

        log.info("\n웹소켓 접속 : " + sessionId + "\n"
                + "룸 UUID : " + room.getRoomId() + "\n"
                + "룸 인원 : " + participantCount);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String roomId = globalVariables.getCheckRoomId().get(sessionId);

        //전역 함수에서 checkRoomIdCount map 을 가져와 해당 룸이 있는지 확인
        if(globalVariables.getCheckRoomIdCount().containsKey(roomId)){
            if(globalVariables.getCheckRoomIdCount().get(roomId) - 1 <= 0){
                //만약 해당 roomId의 유저가 0 이하라면 삭제한다.
                globalVariables.getCheckRoomIdCount().remove(roomId);
            }
            else{
                //아니면 해당 roomId의 유저를 -1 해준다.
                globalVariables.getCheckRoomIdCount().put(roomId, globalVariables.getCheckRoomIdCount().get(roomId) - 1);
            }
        }

        //전역 함수에 roomCheckWaitingClient map 을 가져와 해당 룸이 있는지 확인 한다.
        if(globalVariables.getRoomCheckWaitingClient().containsKey(roomId)){
            Map<String , String> returnMap = new HashMap<>();

            returnMap.put("camKey", globalVariables.getCheckCamKey().get(sessionId));
            returnMap.put("roomCount", String.valueOf(globalVariables.getCheckRoomIdCount().get(roomId)));

            //해당 roomCheckWaitingClient 에서 DeferredResult 에 setResult를 보내어서 해당되는 /poll/leave/room/{roomId} api에 신호를 보낸다.
            globalVariables.getRoomCheckWaitingClient().get(roomId).setResult(
                    new ResponseEntity<>(CommonResp.builder()
                            .data(returnMap)
                            .status_code(HttpStatus.OK.value())
                            .result(CommonResp.ResultType.SUCCESS)
                            .build(),
                            HttpStatus.OK)
            );
        }



        log.info("\n웹소켓 끊김 : " + sessionId+"\n"
                +"룸 ID : " + roomId + "\n"
                +"룸 인원 : " + globalVariables.getCheckRoomIdCount().get(roomId) );
    }


    //SessionConnectedEvent 에서 NativeHeader 찾기 메서드
    private Map<String, List<String>> getNativeHeaders(SessionConnectedEvent event){
        //messageHeaders 를 추출
        MessageHeaders headers = event.getMessage().getHeaders();
        //simpConnectMessage 를 추출
        GenericMessage<?> simpConnectMessage = (GenericMessage<?>) headers.get("simpConnectMessage");
        //simpConnectMessage 의 MessageHeader 를 추출
        MessageHeaders simpHeaders = Objects.requireNonNull(simpConnectMessage).getHeaders();

        //Map<String, List<String>>로 nativeHeader를 추출하여 리턴한다.
        return (Map<String, List<String>>) simpHeaders.get("nativeHeaders");
    }
}
