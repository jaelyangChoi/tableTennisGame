package my.tableTennisGame.web;

import my.tableTennisGame.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {


    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>(new ApiResponse<>(200, "API 요청이 성공했습니다.", null), HttpStatus.OK);
    }
}
