package my.tableTennisGame.web.dto.play;

import lombok.Getter;
import lombok.Setter;

public class PlayReqDto {

    @Getter
    @Setter
    public static class PlayStartReqDto {
        private int userId;
    }

    @Getter
    @Setter
    public static class TeamChangeReqDto{
        private int userId;
    }
}
