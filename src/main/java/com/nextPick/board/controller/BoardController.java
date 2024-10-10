package com.nextPick.board.controller;

import com.nextPick.board.dto.BoardDto;
import com.nextPick.board.service.BoardService;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
@RestController
@RequestMapping("/boards")
@Validated
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/{dtype}")
    public ResponseEntity<BoardDto.Response> createBoard(@PathVariable String dtype,
                                                         @Valid @RequestBody BoardDto.Post postDto) {
        BoardDto.Response response = boardService.createBoard(postDto, dtype);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> updateBoard(@PathVariable Long boardId, @Valid @RequestBody BoardDto.Patch patchDto) {
        BoardDto.Response response = boardService.updateBoard(boardId, patchDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{boardId}/likes")
    public ResponseEntity<Void> toggleLike(@PathVariable Long boardId ) {
        boardService.toggleLike(boardId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable Long boardId) {
        BoardDto.Response response = boardService.getBoardAndIncrementViewCount(boardId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable long boardId) {
        boardService.deleteBoard(boardId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/R")
    public ResponseEntity<List<BoardDto.Response>> getReviewBoards() {
        List<BoardDto.Response> responses = boardService.getBoardsByDtype("R");
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


    @GetMapping("/Q")
    public ResponseEntity<List<BoardDto.Response>> getQuestionBoards() {
        List<BoardDto.Response> responses = boardService.getBoardsByDtype("Q");
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }


}


