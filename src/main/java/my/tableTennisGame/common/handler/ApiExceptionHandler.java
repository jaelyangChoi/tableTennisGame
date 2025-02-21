package my.tableTennisGame.common.handler;

import lombok.extern.slf4j.Slf4j;
import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.web.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    // 잘못된 API 요청
    @ExceptionHandler(WrongRequestException.class)
    public ResponseEntity<?> handleWrongRequestException(WrongRequestException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.wrong(null));
    }

    // 서버 내부 오류 처리 (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(null));
    }
}
