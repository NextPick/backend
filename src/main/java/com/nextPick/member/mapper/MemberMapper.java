package com.nextPick.member.mapper;

import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post post);
    MemberDto.Response memberToResponseDto(Member member);
    Member memberAdminPatchDtoToMember(MemberDto.AdminPatch adminPatch);
//    Member memberPatchToMember(MemberDto.Patch patch);

    default List<MemberDto.Response> memberListToMemberListDtoResponse(List<Member> memberList){
        return memberList
                .stream()
                .map(members -> MemberDto.Response
                        .builder()
                        .memberId(members.getMemberId())
                        .name(members.getName())
                        .gender(members.getGender())
                        .email(members.getEmail())
                        .nickname(members.getNickname())
                        .occupation(members.getOccupation())
                        .status(members.getStatus())
                        .type(members.getType())
                        .career(members.getCareer())
                        .roles(members.getRoles())
                        .build())
                .collect(Collectors.toList());
    }
}
