package my.tableTennisGame.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import my.tableTennisGame.web.dto.room.RoomReqDto;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomAttentionReqDto;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomOutReqDto;
import my.tableTennisGame.web.dto.room.RoomRespDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "방 API", description = "게임 방 생성, 전체 조회, 상세 조회 참가, 나가기 기능")
public interface RoomControllerDocs {

    @Operation(summary = "방 생성 API", description = "방을 생성하려는 user의 조건을 검증한 뒤 방, 방-유저 매핑 데이터을 생성한다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "유저 Id, 방 타입(단식/복식), 방 제목 정보", content = @Content(schema = @Schema(implementation = RoomCreateReqDto.class)))
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<?> createRoom(@RequestBody RoomCreateReqDto roomCreateReqDto);

    @Operation(summary = "방 전체 조회 API", description = "방 목록을 전체 조회한다.")
    @Parameter(name = "pageable", description = "페이지 정보")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<RoomRespDto.RoomListRespDto> getAllRooms(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable);

    @Operation(summary = "방 상세 조회 API", description = "방 id를 파라미터로 전달하면 상세 정보를 조회한다.")
    @Parameter(name = "roomId", description = "방 id")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<RoomRespDto.RoomDetailDto> getRoomDetails(@PathVariable int roomId);

    @Operation(summary = "방 참가 API", description = "방 id와 유저 id를 전달하면 해당 유저를 방에 참가시킨다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "유저 Id", content = @Content(schema = @Schema(implementation = RoomAttentionReqDto.class)))
    @Parameter(name = "roomId", description = "방 id")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<?> participateToRoom(@PathVariable int roomId, @RequestBody RoomAttentionReqDto roomAttentionReqDto);

    @Operation(summary = "방 나가기 API", description = "방 id와 유저 id를 전달하면 해당 유저를 방에서 나가기 처리한다. 호스트가 나가면 참여자가 모두 나가고 방이 FINISH 상태가 된다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "유저 Id", content = @Content(schema = @Schema(implementation = RoomOutReqDto.class)))
    @Parameter(name = "roomId", description = "방 id")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<?> outTheRoom(@PathVariable int roomId, @RequestBody RoomOutReqDto roomOutReqDto);
}
