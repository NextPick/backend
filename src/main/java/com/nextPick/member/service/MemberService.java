package com.nextPick.member.service;

import com.nextPick.auth.utils.CustomAuthorityUtils;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomAuthorityUtils authorityUtils;

    public Member createMember(Member member) {
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);
        List<String> roles = authorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);
        return memberRepository.save(member);
    }

    public Member findMember(Member member){
//        Member member = extaracfdkl
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return findMember;
    }

    public Member updateMember(MemberDto.Patch member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        if(!member.getNickname().isEmpty())
            findMember.setNickname(member.getNickname());
        if(!member.getOccupation().getStatus().isEmpty())
            findMember.setOccupation(member.getOccupation());
        if(member.getGuiltyScore() != null)
            findMember.setGuiltyScore(member.getGuiltyScore());
        if(!member.getPassword().isEmpty()){
            if(member.getConfirmPassword().isEmpty() || !member.getPassword().equals(member.getConfirmPassword()))
                throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        return memberRepository.save(findMember);
    }

    public void deleteMember(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        member.setStatus(Member.memberStatus.DELETED);
        memberRepository.save(member);
    }

    public boolean dupCheckEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        return member != null;
    }

    public boolean dupCheckNickname(String nickname){
        Member member = memberRepository.findByNickname(nickname).orElse(null);
        return member != null;
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(()->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

}
