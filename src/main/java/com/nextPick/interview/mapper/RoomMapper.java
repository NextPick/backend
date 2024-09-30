package com.nextPick.interview.mapper;

import com.nextPick.interview.dto.RoomDto;
import com.nextPick.interview.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(source = "memberId", target = "member.memberId")
    Room roomDtoPostToRoom(RoomDto.Post requestBody);

    default RoomDto.Response roomsCountToRoomDtoResponse(Long roomsCount) {
        RoomDto.Response response = new RoomDto.Response(roomsCount);

        return response;
    }
}
