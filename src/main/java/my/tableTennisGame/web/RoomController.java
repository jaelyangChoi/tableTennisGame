package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.docs.RoomControllerDocs;
import my.tableTennisGame.web.dto.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import static my.tableTennisGame.web.dto.room.RoomRespDto.RoomListRespDto;

@RequiredArgsConstructor
@RequestMapping("/room")
@RestController
public class RoomController implements RoomControllerDocs {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<?> createRoom(@RequestBody RoomCreateReqDto roomCreateReqDto) {
        roomService.createRoom(roomCreateReqDto);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<RoomListRespDto> getAllRooms(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        RoomListRespDto roomListRespDto = roomService.findRoomList(pageable);
        return ApiResponse.success(roomListRespDto);
    }
}
