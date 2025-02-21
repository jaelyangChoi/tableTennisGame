package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.docs.RoomControllerDocs;
import my.tableTennisGame.web.dto.ApiResponse;
import my.tableTennisGame.web.dto.room.RoomReqDto;
import my.tableTennisGame.web.dto.room.RoomRespDto.RoomDetailDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static my.tableTennisGame.web.dto.room.RoomReqDto.*;
import static my.tableTennisGame.web.dto.room.RoomReqDto.RoomAttentionReqDto;
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
        RoomListRespDto roomListRespDto = roomService.getRoomList(pageable);
        return ApiResponse.success(roomListRespDto);
    }

    @GetMapping("/{roomId}")
    public ApiResponse<RoomDetailDto> getRoomDetails(@PathVariable int roomId) {
        RoomDetailDto roomDetailDto = roomService.getRoomDetails(roomId);
        return ApiResponse.success(roomDetailDto);
    }

    @PostMapping("/attention/{roomId}")
    public ApiResponse<?> participateToRoom(@PathVariable int roomId, @RequestBody RoomAttentionReqDto roomAttentionReqDto) {
        roomService.participateToRoom(roomId, roomAttentionReqDto.getUserId());
        return ApiResponse.success(null);
    }

    @PostMapping("/out/{roomId}")
    public ApiResponse<?> outTheRoom(@PathVariable int roomId, @RequestBody RoomOutReqDto roomOutReqDto) {
        roomService.out(roomId, roomOutReqDto.getUserId());
        return ApiResponse.success(null);
    }

}
