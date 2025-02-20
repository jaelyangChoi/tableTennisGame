package my.tableTennisGame.service;

import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.Team;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.repository.UserRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRoomServiceTest extends DummyObject {

    @Mock
    private UserRoomRepository userRoomRepository;

    @InjectMocks
    private UserRoomService userRoomService;

    @DisplayName("방에 참여한 유저 매핑 엔티티 생성 테스트")
    @Test
    void addUserToRoom() {
        // given
        int userId = 1;
        String roomType = "SINGLE";
        String roomStatus = "WAIT";
        User mockHost = newMockUser(userId);
        Room mockRoom = newMockRoom(1, mockHost, roomType, roomStatus);
        when(userRoomRepository.save(any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when
        UserRoom userRoom = userRoomService.addUserToRoom(mockHost, mockRoom, Team.RED);

        // then
        assertNotNull(userRoom);
        assertEquals(mockHost, userRoom.getUser());
        assertEquals(userId, userRoom.getUser().getId());
        assertEquals(mockRoom, userRoom.getRoom());
        assertEquals(roomType, userRoom.getRoom().getRoomType().name());
    }

}