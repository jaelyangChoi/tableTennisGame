package my.tableTennisGame.web.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "ApiResponse", description = "API 응답 모델")
public class ApiResponseSchema {

    @Schema(description = "상태 코드 (200:정상, 201:잘못된 요청, 500:오류)", example = "200")
    private Integer code;

    @Schema(description = "메시지", example = "요청이 성공했습니다.")
    private String message;

    @Schema(description = "데이터. 없을 경우 응답에 포함되지 않음")
    private Object result;
}
