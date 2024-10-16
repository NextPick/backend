package com.nextPick.interview.mapper;

import com.nextPick.interview.dto.RoomDto;
import com.nextPick.interview.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    Room roomDtoPostToRoom(RoomDto.Post requestBody);

    default RoomDto.Response roomsCountToRoomDtoResponse(int roomsCount) {
        RoomDto.Response response = new RoomDto.Response();
        response.setRoom_count(roomsCount);

        return response;
    }

    default RoomDto.PostResponse roomToRoomDtoPostResponse(Room room) {
        RoomDto.PostResponse response = new RoomDto.PostResponse();
        response.setRoomId(room.getRoomId());
        response.setRoomOccupation(room.getOccupation());
        response.setRoomUuid(room.getUuid());
        response.setMemberId(room.getMember().getMemberId());
        response.setTitle(room.getTitle());

        return response;
    }
}
