package my.tableTennisGame.web.dto.room;

import lombok.Getter;
import lombok.Setter;
import my.tableTennisGame.domain.room.Room;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class RoomRespDto {

    @Getter
    @Setter
    public static class RoomListRespDto {
        private int totalElements;
        private int totalPages;
        private List<RoomDto> roomList = new ArrayList<>();

        public RoomListRespDto(Page<Room> roomPage) {
            this.totalElements = (int) roomPage.getTotalElements();
            this.totalPages = roomPage.getTotalPages();
            this.roomList = roomPage.getContent().stream()
                    .map(RoomDto::new).toList();
        }

        @Getter
        @Setter
        public static class RoomDto {
            private int id;
            private String title;
            private int hostId;
            private String roomType; // SINGLE(단식), DOUBLE(복식)
            private String status; // WAIT(대기), PROGRESS(진행중), FINISH(완료)

            public RoomDto(Room room) {
                this.id = room.getId();
                this.title = room.getTitle();
                this.hostId = room.getHost().getId();
                this.roomType = room.getRoomType().name();
                this.status = room.getStatus().name();
            }
        }
    }
}
