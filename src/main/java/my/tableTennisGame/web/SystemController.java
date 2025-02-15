package my.tableTennisGame.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.init.ExternalApiService;
import my.tableTennisGame.service.init.InitService;
import my.tableTennisGame.web.dto.ApiResponse;
import my.tableTennisGame.web.dto.init.FakerApiRespDto;
import my.tableTennisGame.web.dto.init.InitReqDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="시스템 API", description = "헬스 체크, 초기화")
@RestController
@RequiredArgsConstructor
public class SystemController {

    private final InitService initService;
    private final ExternalApiService externalApiService;

    /**
     * 1. 헬스체크 API
     */
    @Operation(summary = "헬스 체크 API", description = "서버의 상태를 체크")
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
    }

    /**
     * 2. 초기화 API
     */
    @Operation(summary = "초기화 API", description = "모든 테이블 데이터를 삭제하고 fakerAPI 호출하여 회원 정보 초기화")
    @Parameter(name="seed", description = "데이터 생성 seed 값")
    @Parameter(name="quantity", description = "생성할 회원 데이터 수")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @PostMapping("/init")
    public ResponseEntity<?> init(@RequestBody InitReqDto reqDto) {
        // 1. 테이블 초기화
        initService.resetDatabase();

        // 2. 전달받은 데이터로 fakerAPI 호출
        List<FakerApiRespDto.UserDto> userDtos = externalApiService.fetchFakers(reqDto.getSeed(), reqDto.getQuantity());

        // 3. 서비스에 필요한 회원 정보 저장
        initService.saveInitData(userDtos);

        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
    }

}
