package com.nextPick.member.controller;

import com.nextPick.dto.MultiResponseDto;
import com.nextPick.dto.SingleResponseDto;
import com.nextPick.helper.email.EmailVerificationService;
import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.dto.VerificationRequest;
import com.nextPick.member.entity.Member;
import com.nextPick.member.mapper.MemberMapper;
import com.nextPick.member.service.MemberService;
import com.nextPick.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@RestController
@Validated
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final static String MEMBER_DEFAULT_URL = "/members";
    private final MemberService service;
    private final MemberMapper memberMapper;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/auth-code")
    public ResponseEntity signUpMember(@Valid @RequestBody VerificationRequest verificationRequest) {
        emailVerificationService.sendCodeToEmail(verificationRequest.getEmail());
        return ResponseEntity.accepted().body("이메일로 인증 코드를 전송했습니다. 인증 코드를 입력하여 회원가입을 완료하세요.");
    }

    @PostMapping("/verify-auth-code")
    public ResponseEntity verifyEmail(@Valid @RequestBody VerificationRequest verificationRequest) {
        String email = verificationRequest.getEmail();
        String authCode = verificationRequest.getAuthCode();

        boolean isVerified = emailVerificationService.verifyCode(email, authCode);
        if (!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 코드가 올바르지 않습니다.");
        }
        return ResponseEntity.ok("이메일 인증이 완료되었습니다. 회원가입을 진행하세요.");
    }

    /**
     * 회원가입 하는 메서드
     *
     * @param requestBody 회원가입을 위해 받은 requestBody
     *
     * @return 201 : Created  & 409 : Conflict
     */
    @PostMapping
    public ResponseEntity createMember(@Valid @RequestBody MemberDto.Post requestBody) {
        Member member = memberMapper.memberPostToMember(requestBody);
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


    @GetMapping("/mentor")
    public ResponseEntity getMembers(@Positive @RequestParam int page,
                                     @Positive @RequestParam int size) {
        Page<Member> findPageMembers = service.findMemberPage(page-1,size);
        List<Member> findListMembers = findPageMembers.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(memberMapper.memberListToMemberListDtoResponse(findListMembers), findPageMembers),
                HttpStatus.OK);
    }


    @PatchMapping("/admin/{member-id}")
    public ResponseEntity patchMember(@PathVariable("member-id") @Positive long memberId,
                                      @Valid @RequestBody MemberDto.AdminPatch adminPatch) {
        Member member = service.updateMemberForAdmin(memberMapper.memberAdminPatchDtoToMember(adminPatch),memberId);
        return new ResponseEntity<>(
                new SingleResponseDto<>(memberMapper.memberToResponseDto(member)), HttpStatus.OK);
    }

    @DeleteMapping("/admin/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") @Positive long memberId) {
        service.deleteMember(memberId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity changeStatusToDeleteMember() {
        service.changeStatusToDelete();
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
        return service.dupCheckNickname(duplicationNicknameCheck.getNickname()) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}