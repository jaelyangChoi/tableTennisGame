package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.InitService;
import my.tableTennisGame.web.dto.ApiResponse;
import my.tableTennisGame.web.dto.init.FakerApiRespDto;
import my.tableTennisGame.web.dto.init.InitReqDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final InitService initService;

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

        // 2. 전달받은 데이터로 fakerAPI 호출하여 서비스에 필요한 회원 정보 저장
        String url = generateApiUrl(initReqDto);

        // 2-1. 외부 API 호출
        ResponseEntity<FakerApiRespDto> response = new RestTemplate().getForEntity(url, FakerApiRespDto.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null)
            throw new IllegalStateException();

        // 2-2. 사용자 데이터 추출하여 테이블 초기화
        initService.intDatabase(response.getBody().getData());

        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
    }

    /**
     * 전달받은 seed 와 quantity 값을 이용해 외부 API URL 생성
     */
    private String generateApiUrl(InitReqDto initReqDto) {
        String seed = initReqDto.getSeed();
        String quantity = initReqDto.getQuantity();
        return String.format("https://fakerapi.it/api/v1/users?_seed=%s&_quantity=%s&_locale=ko_KR", seed, quantity);
    }
}
