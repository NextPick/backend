package com.nextPick.member.mapper;

import com.nextPick.member.dto.MemberDto;
import com.nextPick.member.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post post);
    MemberDto.Response memberToResponseDto(Member member);

}
