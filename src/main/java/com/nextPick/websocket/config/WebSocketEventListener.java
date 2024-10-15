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
import com.nextPick.member.repository.MemberRepository;
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

    @Autowired
    private MemberRepository memberRepository;

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
        Room room = roomService.findActiveRoom(occupation, member);
        String roomUUid = room.getUuid();

        // 해당 세션 Id에 대한 룸 Id 가 있는지 확인
        if(room.getSessionId() == null) {
            //없다면 추가 해준다.
            room.setSessionId(sessionId);
        }

        participantService.createParticipant(room, member, sessionId, camKey);

        room = roomRepository.save(room);

        int participantCount = participantService.findParticipantCount(room.getUuid());

        messagingTemplate.convertAndSend("topic/memberType/" + sessionId, member.getType());
        messagingTemplate.convertAndSend("/topic/memberId/" + sessionId, member.getMemberId());
        messagingTemplate.convertAndSend("/topic/roomUuid/" + sessionId, roomUUid);

        log.info("\n웹소켓 접속 : " + sessionId + "\n"
                + "룸 UUID : " + room.getRoomId() + "\n"
                + "룸 인원 : " + participantCount);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = headerAccessor.getSessionId();

        // member 찾기
        Participant findParticipant = participantService.findParticipantBySessionId(sessionId);

        Member member  = memberRepository.findById(
                        findParticipant.getMember().getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        Long memberId = member.getMemberId();

        // 룸 아이디를 이용해서 룸 찾기
        Long roomId = findParticipant.getRoom().getRoomId();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ROOM_NOT_FOUND));
        String roomUUid = room.getUuid();

        // roomUUid로 룸의 전체 참가자 찾기
        List<Participant> participantList = participantService.findParticipants(roomUUid);

        // memberId로 참가자 비교 후 삭제
        for (Participant participant : participantList) {
            if (participant.getMember().getMemberId() == memberId) {
                participantRepository.delete(participant);
            }
        }

        // 지운 후 룸의 참가자 수 찾기
        int participantCount = participantService.findParticipantCount(roomUUid);

        // 방의 참가자가 0명일 경우 방 삭제
        if (participantCount == 0) {
            roomRepository.delete(room);
        }

        log.info("\n웹소켓 끊김 : " + sessionId+"\n"
                +"룸 ID : " + roomId + "\n"
                +"룸 인원 : " + participantCount );
    }


    private Map<String, List<String>> getNativeHeaders(GenericMessage<?> eventMessage) {
        // messageHeaders 를 추출
        MessageHeaders headers = eventMessage.getHeaders();
        // simpConnectMessage 를 추출
        GenericMessage<?> simpConnectMessage = (GenericMessage<?>) headers.get("simpConnectMessage");
        // simpConnectMessage 의 MessageHeader 를 추출
        MessageHeaders simpHeaders = Objects.requireNonNull(simpConnectMessage).getHeaders();

        // Map<String, List<String>>로 nativeHeader를 추출하여 리턴한다.
        return (Map<String, List<String>>) simpHeaders.get("nativeHeaders");
    }

    // SessionConnectedEvent 에서 NativeHeader 찾기 메서드
    private Map<String, List<String>> getNativeHeaders(SessionConnectedEvent event) {
        return getNativeHeaders((GenericMessage<?>) event.getMessage());
    }

    // SessionDisconnectEvent 에서 NativeHeader 찾기 메서드
    private Map<String, List<String>> getNativeHeaders(SessionDisconnectEvent event) {
        return getNativeHeaders((GenericMessage<?>) event.getMessage());
    }
}
