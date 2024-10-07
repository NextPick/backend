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
}
