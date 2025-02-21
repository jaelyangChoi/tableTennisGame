package my.tableTennisGame.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import my.tableTennisGame.web.dto.play.PlayReqDto.PlayStartReqDto;
import my.tableTennisGame.web.dto.play.PlayReqDto.TeamChangeReqDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "게임 API", description = "게임 시작, 팀 변경 기능")
public interface PlayControllerDocs {

    @Operation(summary = "게임 시작 API", description = "게임을 시작하고 1분 뒤 종료한다.")
    @Parameter(name = "roomId", description = "방 id")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "유저 Id", content = @Content(schema = @Schema(implementation = PlayStartReqDto.class)))
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<?> startGame(@PathVariable int roomId, @RequestBody PlayStartReqDto playStartReqDto);

    @Operation(summary = "팀 변경 API", description = "상대 팀으로 이동한다.")
    @Parameter(name = "roomId", description = "방 id")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "유저 Id", content = @Content(schema = @Schema(implementation = TeamChangeReqDto.class)))
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<?> changeTeam(@PathVariable int roomId, @RequestBody TeamChangeReqDto teamChangeReqDto);
}
