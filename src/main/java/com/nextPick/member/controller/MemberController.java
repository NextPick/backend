package com.nextPick.member.controller;

import com.nextPick.dto.SingleResponseDto;
import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.entity.Member;
import com.nextPick.member.mapper.MemberMapper;
import com.nextPick.member.service.MemberService;
import com.nextPick.questionList.controller.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@Validated
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService service;
    private final MemberMapper memberMapper;
//    private final EmailVerificationService emailVerificationService;
//    private final AuthService authService;

    //이메일 인증코드 전송
//    @PostMapping("/auth-code")
//    public ResponseEntity signUpMember(@Valid @RequestBody VerificationRequest verificationRequest) {
//        emailVerificationService.sendCodeToEmail(verificationRequest.getEmail());
//        return ResponseEntity.accepted().body("이메일로 인증 코드를 전송했습니다. 인증 코드를 입력하여 회원가입을 완료하세요.");
//    }

    //인증코드 검증
//    @PostMapping("/verify-auth-code")
//    public ResponseEntity verifyEmail(@Valid @RequestBody VerificationRequest verificationRequest) {
//        String email = verificationRequest.getEmail();
//        String authCode = verificationRequest.getAuthCode();
//
//        boolean isVerified = emailVerificationService.verifyCode(email, authCode);
//        if (!isVerified) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 코드가 올바르지 않습니다.");
//        }
//        return ResponseEntity.ok("이메일 인증이 완료되었습니다. 회원가입을 진행하세요.");
//    }

    /**
     * 회원가입 하는 메서드
     *
     * @param memberDto 회원가입을 위해 받은 memberDto
     *
     * @return 201 : Created  & 409 : Conflict
     */
    @PostMapping
    public ResponseEntity createMember(@Valid @RequestBody MemberDto.Post memberDto) {
        Member member = memberMapper.memberPostToMember(memberDto);
        service.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity getMember() {
        Member findMember = service.findMember();
        return new ResponseEntity<>(
                new SingleResponseDto<>(memberMapper.memberToResponseDto(findMember)), HttpStatus.OK);
    }

//    @PatchMapping
//    public ResponseEntity patchMember(@Valid @RequestBody MemberDto.Patch patch,
//                                      @AuthenticationPrincipal Object principal) {
//        patch.setEmail(principal.toString());
//        Member member = service.updateMember(patch,principal);
//        return new ResponseEntity<>(
//                new SingleResponseDto<>(memberMapper.memberToResponseDto(member)), HttpStatus.OK);
//    }

    @DeleteMapping
    public ResponseEntity deleteMember() {
        service.deleteMember();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/verify/email")
    public ResponseEntity emailDuplicationVerify(@Valid @RequestBody MemberDto.DuplicationEmailCheck duplicationEmailCheck) {
        return service.dupCheckEmail(duplicationEmailCheck.getEmail()) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @PostMapping("/verify/nickname")
    public ResponseEntity nicknameDuplicationVerify(@Valid @RequestBody MemberDto.DuplicationNicknameCheck duplicationNicknameCheck){
        return service.dupCheckEmail(duplicationNicknameCheck.getNickName()) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
