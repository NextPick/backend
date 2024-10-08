package com.nextPick.member.service;

import com.nextPick.auth.utils.CustomAuthorityUtils;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.utils.ExtractMemberAndVerify;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService extends ExtractMemberAndVerify {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;

    public Member createMember(Member member) {
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(m -> { throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);});
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);
        return memberRepository.save(member);
    }

    public Member findMember(){
        return extractMemberFromPrincipal(memberRepository);
    }

    public Member updateMemberForAdmin(Member member,long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Optional.ofNullable(member.getStatus())
                .ifPresent(status -> findMember.setStatus(status));
        Optional.ofNullable(member.getGuiltyScore())
                .ifPresent(guiltyScore -> findMember.setGuiltyScore(findMember.getGuiltyScore()+guiltyScore));

        if(findMember.getGuiltyScore() >= 5){
            findMember.setStatus(Member.memberStatus.BAN);
        }
        return memberRepository.save(findMember);
    }

//    public Member updateMember(MemberDto.Patch member) {
//        Member findMember = extractMemberFromPrincipal(memberRepository);
//
//        if(!member.getNickname().isEmpty())
//            findMember.setNickname(member.getNickname());
//        if(!member.getOccupation().getStatus().isEmpty())
//            findMember.setOccupation(member.getOccupation());
//        if(member.getGuiltyScore() != null)
//            findMember.setGuiltyScore(member.getGuiltyScore());
//        if(!member.getPassword().isEmpty()){
//            if(member.getConfirmPassword().isEmpty() || !member.getPassword().equals(member.getConfirmPassword()))
//                throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
//        }
//        return memberRepository.save(findMember);
//    }

    public void deleteMember() {
        Member member = extractMemberFromPrincipal(memberRepository);
        member.setStatus(Member.memberStatus.DELETED);
        memberRepository.save(member);
    }

    public boolean dupCheckEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        return member == null;
    }

    public boolean dupCheckNickname(String nickname){
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        return member == null;
    }

}
