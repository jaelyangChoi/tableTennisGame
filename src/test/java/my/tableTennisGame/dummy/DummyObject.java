package my.tableTennisGame.dummy;

import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.room.RoomType;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

//상속 받은 클래스에서 해당 메소드 사용
public class DummyObject {

    protected User newMockUser(int id) {
        return User.builder()
                .id(id)
                .name("test" + id)
                .build();
    }

    protected List<User> newMockUsers(int fromId, int toId, UserStatus userStatus) {
        return IntStream.range(fromId, toId + 1)
                .mapToObj(i -> {
                    User user = User.builder()
                            .id(i)
                            .fakerId(i)
                            .name("test" + i)
                            .status(userStatus)
                            .build();
                    ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
                    ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());
                    return user;
                })
                .toList();
    }

    protected Room newMockRoom(int id, User host, String roomType, String roomStatus) {
        Room room = Room.builder()
                .id(id)
                .host(host)
                .roomType(RoomType.valueOf(roomType))
                .status(RoomStatus.valueOf(roomStatus))
                .build();
        ReflectionTestUtils.setField(room, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(room, "updatedAt", LocalDateTime.now());
        return room;
    }

    protected List<Room> newMockRooms(int fromId, int toId, String roomType) {
        return IntStream.range(fromId, toId + 1)
                .mapToObj(i -> {
                    Room room = Room.builder()
                            .id(i)
                            .title("test" + i)
                            .roomType(RoomType.valueOf(roomType))
                            .host(newMockUser(i))
                            .status(RoomStatus.WAIT)
                            .build();
                    ReflectionTestUtils.setField(room, "createdAt", LocalDateTime.now());
                    ReflectionTestUtils.setField(room, "updatedAt", LocalDateTime.now());
                    return room;
                })
                .toList();
    }

}
