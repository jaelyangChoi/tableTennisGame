package my.tableTennisGame.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import my.tableTennisGame.web.dto.room.RoomReqDto;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "방 API", description = "게임 방 생성, 조회 등")
public interface RoomControllerDocs {

    @Operation(summary = "방 생성 API", description = "방을 생성하려는 user의 조건을 검증한 뒤 방, 방-유저 매핑 데이터을 생성한다.")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<?> createRoom(@RequestBody RoomReqDto.RoomCreateReqDto roomCreateReqDto);
}
