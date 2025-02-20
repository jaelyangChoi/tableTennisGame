package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.docs.RoomControllerDocs;
import my.tableTennisGame.web.dto.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;

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
}
