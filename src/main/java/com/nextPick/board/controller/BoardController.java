package com.nextPick.board.controller;

import com.nextPick.board.dto.BoardDto;
import com.nextPick.board.service.BoardService;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.entity.Member;
import com.nextPick.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boards")
@Validated
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping("/{dtype}")
    public ResponseEntity<BoardDto.Response> createBoard(
            @PathVariable String dtype,
            @ModelAttribute @Valid BoardDto.Post postDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        BoardDto.Response response = boardService.createBoard(postDto, dtype, images);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PatchMapping("/{boardId}")
    public ResponseEntity<BoardDto.Response> updateBoard(
            @PathVariable Long boardId,
            @Valid @ModelAttribute BoardDto.Patch patchDto,  // 텍스트 수정
            @RequestParam(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestParam(value = "imagesToDelete", required = false) List<String> imagesToDelete
    ) throws IOException {
        BoardDto.Response response = boardService.updateBoard(boardId, patchDto, newImages, imagesToDelete);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{boardId}/likes")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long boardId) {
        Map<String, Object> response = boardService.toggleLike(boardId);
        return new ResponseEntity<>(response, HttpStatus.OK);
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


    // 리뷰 보드 데이터를 가져오는 메서드
    @GetMapping("/Q")
    public ResponseEntity<?> getReviewBoards(
            @Positive @RequestParam int page,
            @Positive @RequestParam int size,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(required = false, defaultValue = "*") String keyword) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<BoardDto.Response> boardPage = boardService.getBoardsByDtype("Q", sort, keyword, pageable);
            List<BoardDto.Response> responses = boardPage.getContent();
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/R")
    public ResponseEntity<List<BoardDto.Response>> getQuestionBoards(
            @Positive @RequestParam int page,
            @Positive @RequestParam int size,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(required = false, defaultValue = "*") String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<BoardDto.Response> boardPage = boardService.getBoardsByDtype("R", sort, keyword, pageable);
        List<BoardDto.Response> responses = boardPage.getContent();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }






}


