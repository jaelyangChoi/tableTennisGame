package my.tableTennisGame.web.dto.common;

import lombok.Data;

import java.util.List;

@Data
public class FakerApiRespDto {
    private List<UserDto> data;

    @Data
    public static class UserDto {
        private int id;
        private String username;
        private String email;
    }
}
