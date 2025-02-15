package my.tableTennisGame.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final Integer code;
    private final String message;
    private final T result;


    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<T>(200, "API 요청이 성공했습니다.", result);
    }

    public static <T> ApiResponse<T> wrong(T result) {
        return new ApiResponse<T>(201, "불가능한 요청입니다.", result);
    }

    public static <T> ApiResponse<T> error(T result) {
        return new ApiResponse<T>(500, "에러가 발생했습니다.", result);
    }
}
