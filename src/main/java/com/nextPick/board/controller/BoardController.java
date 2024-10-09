package com.nextPick.board.controller;

import com.nextPick.board.dto.BoardDto;
import com.nextPick.board.service.BoardService;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping
    public ResponseEntity<BoardDto.Response> createBoard(@Valid @RequestBody BoardDto.Post postDto, @RequestParam Long memberId) {
        BoardDto.Response response = boardService.createBoard(postDto, memberId);
        if (response == null) {
            throw new BusinessLogicException(ExceptionCode.INVALID_BOARD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> updateBoard(@PathVariable Long boardId, @Valid @RequestBody BoardDto.Patch patchDto) {
        BoardDto.Response response = boardService.updateBoard(boardId, patchDto);
        if (response == null) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> getBoard(@PathVariable Long boardId, @RequestParam Long memberId) {
        BoardDto.Response response = boardService.getBoardAndIncrementViewCount(boardId, memberId);
        if (response == null) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }




    @PostMapping("/{boardId}/like/{memberId}")
    public ResponseEntity<Void> toggleLike(@PathVariable Long boardId, @PathVariable Long memberId) {
        try {
            boardService.toggleLike(boardId, memberId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND);
        }
    }

    @GetMapping("/review")
    public ResponseEntity<List<BoardDto.Response>> getReviewBoards() {
        List<BoardDto.Response> responses = boardService.getBoardsByDtype("R");
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/question")
    public ResponseEntity<List<BoardDto.Response>> getQuestionBoards() {
        List<BoardDto.Response> responses = boardService.getBoardsByDtype("Q");
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{board-id}")
    public ResponseEntity deleteBoard(@PathVariable("board-id") @Positive long boardId,
                                      Authentication authentication) {
        String email = null;
        if (authentication != null) {
            email = (String) authentication.getPrincipal();
//            boolean isLoggedOut = !authService.isTokenValid(email);
//            if (isLoggedOut) {
//                return new ResponseEntity<>("User Logged Out", HttpStatus.UNAUTHORIZED);
//            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        boardService.deleteBoard(boardId, authentication);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
