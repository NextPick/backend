package com.nextPick.board.mapper;

import com.nextPick.board.dto.BoardDto;
import com.nextPick.board.entity.Board;
import com.nextPick.board.entity.QuestionBoard;
import com.nextPick.board.entity.ReviewBoard;
import org.mapstruct.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.file.LinkOption;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface BoardMapper{


        // Post DTO -> Board 엔티티로 변환
        default Board postDtoToBoard(BoardDto.Post postDto) {
            Board board;

            // dtype에 따라 적절한 게시판 유형의 엔티티 생성
            if ("ReviewBoard".equals(postDto.getDtype())) {
                board = new ReviewBoard();
            } else if ("QuestionBoard".equals(postDto.getDtype())) {
                board = new QuestionBoard();
            } else {
                throw new IllegalArgumentException("Invalid board type: " + postDto.getContent());
            }

            board.setTitle(postDto.getTitle());
            board.setContent(postDto.getContent());

            // contentImg와 boardCategory 설정 (ReviewBoard에만 적용)
            if (board instanceof ReviewBoard && postDto.getBoardCategory() != null) {
                ((ReviewBoard) board).setBoardCategory(postDto.getBoardCategory());
            }

            return board;
        }

        // Patch DTO -> 기존 Board 엔티티에 수정 내용 반영
        default void patchDtoToBoard(BoardDto.Patch patchDto, Board board) {
            board.setTitle(patchDto.getTitle());
            board.setContent(patchDto.getContent());
        }

        // Board 엔티티 -> Response DTO 변환
        default BoardDto.Response boardToResponse(Board board) {
            return BoardDto.Response.builder()
                    .boardId(board.getBoardId())
                    .title(board.getTitle())
                    .author(board.getMemberNickname())
                    .content(board.getContent())
                    .dtype(board.getClass().getSimpleName())
                    .likesCount(board.getLikesCount())
                    .viewCount(board.getViewCount())
                    .build();
        }

        // List<Board> -> List<BoardDto.Response> 변환
        default List<BoardDto.Response> boardsToResponses(List<Board> boards) {
            return boards.stream()
                    .map(this::boardToResponse)  // 개별 Board를 BoardDto.Response로 변환
                    .collect(Collectors.toList());
        }
    }




