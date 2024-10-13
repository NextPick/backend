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
public interface BoardMapper {

    default void postDtoToBoard(BoardDto.Post postDto, Board board) {
        board.setTitle(postDto.getTitle());
        board.setContent(postDto.getContent());
        board.setBoardStatus(Board.BoardStatus.BOARD_POST);

        if (board instanceof ReviewBoard && postDto.getBoardCategory() != null) {
            ((ReviewBoard) board).setBoardCategory(postDto.getBoardCategory());
        }
    }
    BoardDto.ResponseBoard boardToResponseBoard(Board board);

    default void patchDtoToBoard(BoardDto.Patch patchDto, Board board) {
        board.setTitle(patchDto.getTitle());
        board.setContent(patchDto.getContent());
    }

    default BoardDto.Response boardToResponse(Board board) {
        // 빌더 호출은 Response.builder()로 합니다.
        BoardDto.Response.ResponseBuilder responseBuilder = BoardDto.Response.builder();
        responseBuilder.boardId(board.getBoardId());
        responseBuilder.title(board.getTitle());
        responseBuilder.author(board.getMemberNickname());
        responseBuilder.boardStatus(board.getBoardStatus().getStatusDescription());
        responseBuilder.content(board.getContent());
        responseBuilder.dtype(board.getClass().getSimpleName());
        responseBuilder.likesCount(board.getLikesCount());
        responseBuilder.viewCount(board.getViewCount());
        responseBuilder.commentCount(board.getComments().size());
        responseBuilder.imageUrls(board.getImageUrls());
//                    .boardId(board.getBoardId())
//                .title(board.getTitle())
//                .author(board.getMemberNickname())
//                .content(board.getContent())
//                .dtype(board.getClass().getSimpleName())
//                .likesCount(board.getLikesCount())
//                .viewCount(board.getViewCount())
//                .boardStatus(board.getBoardStatus().getStatusDescription());
        // ReviewBoard일 경우에만 BoardCategory 추가
        if (board instanceof ReviewBoard) {
            ReviewBoard reviewBoard = (ReviewBoard) board;
            responseBuilder.boardCategory(reviewBoard.getBoardCategory());
        }


        return responseBuilder.build();
    }




    default List<BoardDto.Response> boardsToResponses(List<Board> boards) {
        return boards.stream()
                .map(this::boardToResponse)
                .collect(Collectors.toList());
    }
}
