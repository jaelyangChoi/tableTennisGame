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
}
