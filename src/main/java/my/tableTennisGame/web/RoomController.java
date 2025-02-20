package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import static my.tableTennisGame.web.dto.room.RoomReqDto.*;

@RequiredArgsConstructor
@RequestMapping("/room")
@RestController
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<?> createRoom(@RequestBody RoomCreateReqDto roomCreateReqDto){
        roomService.createRoom(roomCreateReqDto);
        return ApiResponse.success(null);
    }
}
