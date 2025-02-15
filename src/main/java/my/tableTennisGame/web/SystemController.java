package my.tableTennisGame.web;

import my.tableTennisGame.common.WrongRequestException;
import my.tableTennisGame.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
    }

    @GetMapping("/wrong")
    public ResponseEntity<?> wrongRequest() {
        throw new WrongRequestException(null);

    }

    @GetMapping("/error")
    public ResponseEntity<?> errorRequest() throws Exception {
        throw new Exception("eeror");

    }
}
