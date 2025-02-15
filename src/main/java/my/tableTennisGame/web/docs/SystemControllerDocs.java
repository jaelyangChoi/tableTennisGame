package my.tableTennisGame.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import my.tableTennisGame.web.dto.init.InitReqDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "시스템 API", description = "헬스 체크, 초기화")
public interface SystemControllerDocs {

    @Operation(summary = "헬스 체크 API", description = "서버의 상태를 체크")
    @ApiResponse(responseCode = "200", description = "헬스 체크 성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public ResponseEntity<?> healthCheck();

    @Operation(summary = "초기화 API", description = "모든 테이블 데이터를 삭제하고 fakerAPI 호출하여 회원 정보 초기화")
    @Parameter(name = "seed", description = "데이터 생성 seed 값")
    @Parameter(name = "quantity", description = "생성할 회원 데이터 수")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public ResponseEntity<?> init(@RequestBody InitReqDto reqDto);
}
