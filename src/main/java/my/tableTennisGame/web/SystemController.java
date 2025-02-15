package my.tableTennisGame.web;

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

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final InitService initService;
    private final ExternalApiService externalApiService;

    /**
     * 1. 헬스체크 API
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
    }

    /**
     * 2. 초기화 API
     */
    @PostMapping("/init")
    public ResponseEntity<?> init(@RequestBody InitReqDto initReqDto) {
        // 1. 테이블 초기화
        initService.resetDatabase();

        // 2. 전달받은 데이터로 fakerAPI 호출
        List<FakerApiRespDto.UserDto> userDtos = externalApiService.fetchFakers(initReqDto.getSeed(), initReqDto.getQuantity());

        // 3. 서비스에 필요한 회원 정보 저장
        initService.saveInitData(userDtos);

        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
    }

}
