package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.tableTennisGame.service.PlayService;
import my.tableTennisGame.web.dto.ApiResponse;
import my.tableTennisGame.web.dto.play.PlayReqDto.PlayStartReqDto;
import my.tableTennisGame.web.dto.play.PlayReqDto.TeamChangeReqDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PlayController {

    private final PlayService playService;

    @PutMapping("/room/start/{roomId}")
    public ApiResponse<?> startGame(@PathVariable int roomId, @RequestBody PlayStartReqDto playStartReqDto) {

        playService.startGame(roomId, playStartReqDto.getUserId());
        return ApiResponse.success(null);
    }


    @PutMapping("/team/{roomId}")
    public ApiResponse<?> changeTeam(@PathVariable int roomId, @RequestBody TeamChangeReqDto teamChangeReqDto) {
        playService.changeTeam(roomId, teamChangeReqDto.getUserId());
        return ApiResponse.success(null);
    }
}
