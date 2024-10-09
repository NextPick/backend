package com.nextPick.boardComment.controller;

import com.nextPick.boardComment.dto.BoardCommentDto;
import com.nextPick.boardComment.entity.BoardComment;
import com.nextPick.boardComment.mapper.BoardCommentMapper;
import com.nextPick.boardComment.service.BoardCommentService;
import com.nextPick.dto.SingleResponseDto;
import com.nextPick.utils.UriCreator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/boards/{board-id}/comments")  // post -> board로 변경
public class BoardCommentController {

    private final BoardCommentService boardCommentService;
    private final BoardCommentMapper mapper;

    public BoardCommentController(BoardCommentService boardCommentService, BoardCommentMapper mapper) {
        this.boardCommentService = boardCommentService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<Void> createComment(@PathVariable("board-id") Long boardId,
                                              @Valid @RequestBody BoardCommentDto.Post requestBody) {
        BoardComment boardComment = mapper.boardCommentPostDtoToBoardComment(requestBody);
        boardCommentService.createBoardComment(boardComment, boardId, requestBody.getMemberId());
        URI location = UriCreator.createUri("/boards/" + boardId + "/comments", boardComment.getBoardCommentId());
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<SingleResponseDto<BoardCommentDto.Response>> updateComment(
            @PathVariable("comment-id") Long commentId,
            @Valid @RequestBody BoardCommentDto.Patch requestBody) {
        BoardComment updatedComment = boardCommentService.updateBoardComment(commentId, mapper.boardCommentPatchDtoToBoardComment(requestBody), requestBody.getMemberId());
        return ResponseEntity.ok(new SingleResponseDto<>(mapper.boardCommentToBoardCommentResponseDto(updatedComment)));
    }

    @GetMapping
    public ResponseEntity<List<BoardCommentDto.Response>> getComments(@PathVariable("board-id") Long boardId) {
        List<BoardComment> boardComments = boardCommentService.findBoardCommentsByBoardId(boardId);
        return ResponseEntity.ok(mapper.boardCommentsToBoardCommentResponseDtos(boardComments));
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("comment-id") Long commentId,
                                              @RequestParam("memberId") Long memberId) {
        boardCommentService.deleteBoardComment(commentId, memberId);
        return ResponseEntity.noContent().build();
    }
}
