package my.tableTennisGame.web.dto.room;

import lombok.Getter;
import lombok.Setter;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.room.RoomType;
import my.tableTennisGame.domain.user.User;

public class RoomReqDto {

    @Getter
    @Setter
    public static class RoomCreateReqDto {
        private int userId;
        private String roomType;
        private String title;

        public Room toEntity(User user) {
            return Room.builder()
                    .roomType(RoomType.valueOf(roomType))
                    .title(title)
                    .status(RoomStatus.WAIT) //방은 초기에 대기(WAIT) 상태로 생성
                    .host(user)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class RoomAttentionReqDto {
        private int userId;
    }

    @Getter
    @Setter
    public static class RoomOutReqDto {
        private int userId;
    }
}
