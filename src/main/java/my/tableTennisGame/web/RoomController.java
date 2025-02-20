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
        //todo: user-room 생성해야 함. 트랜잭션 처리 어떻게 하지..? 서비스에서 하는게 맞을 듯.
        return ApiResponse.success(null);
    }
}
