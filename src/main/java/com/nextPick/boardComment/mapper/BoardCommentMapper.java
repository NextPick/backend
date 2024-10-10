package com.nextPick.boardComment.mapper;

import com.nextPick.boardComment.dto.BoardCommentDto;
import com.nextPick.boardComment.entity.BoardComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BoardCommentMapper {

    @Mapping(target = "parentComment", expression = "java(requestBody.getParentCommentId() != null ? new BoardComment(requestBody.getParentCommentId()) : null)")
    BoardComment boardCommentPostDtoToBoardComment(BoardCommentDto.Post requestBody);

    BoardComment boardCommentPatchDtoToBoardComment(BoardCommentDto.Patch requestBody);

    @Mapping(source = "member.memberId", target = "memberId")
    @Mapping(source = "board.boardId", target = "boardId")
    @Mapping(source = "member.nickname", target = "nickname")
    @Mapping(source = "parentComment.boardCommentId", target = "parentCommentId")
    BoardCommentDto.Response boardCommentToBoardCommentResponseDto(BoardComment boardComment);

    List<BoardCommentDto.Response> boardCommentsToBoardCommentResponseDtos(List<BoardComment> boardComments);
}
