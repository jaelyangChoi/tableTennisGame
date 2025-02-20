package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.InitService;
import my.tableTennisGame.web.docs.SystemControllerDocs;
import my.tableTennisGame.web.dto.ApiResponse;
import my.tableTennisGame.web.dto.init.InitReqDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SystemController implements SystemControllerDocs {

    private final InitService initService;

    /**
     * 1. 헬스체크 API
     */
    @GetMapping("/health")
    public ApiResponse<?> healthCheck() {
        return ApiResponse.success(null);
    }


    /**
     * 2. 초기화 API
     */
    @PostMapping("/init")
    public ApiResponse<?> init(@RequestBody InitReqDto reqDto) {
        initService.init(reqDto);
        return ApiResponse.success(null);
    }

}
