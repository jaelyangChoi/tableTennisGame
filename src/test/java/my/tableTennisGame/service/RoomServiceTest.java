package my.tableTennisGame.service;

import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.repository.RoomRepository;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest extends DummyObject {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRoomService userRoomService;

    @InjectMocks
    private RoomService roomService;

    @DisplayName("방 생성 서비스 테스트")
    @Test
    void createRoom() {
        // given
        int userId = 1;
        String roomType = "SINGLE";
        RoomCreateReqDto roomCreateReqDto = createRequest(userId, roomType);
        User mockHost = newMockUser(userId, "jaeryang");
        Room mockRoom = newMockRoom(1, mockHost, roomType);

        when(userService.getValidUser(userId)).thenReturn(mockHost);
        when(roomRepository.save(any())).thenReturn(mockRoom);

        // when
        roomService.createRoom(roomCreateReqDto);

        verify(userService).getValidUser(userId);
        verify(roomRepository).save(any());
        verify(userRoomService).addUserToRoom(mockHost, mockRoom);
        verifyNoMoreInteractions(userService, roomRepository, userRoomService);
    }

    private RoomCreateReqDto createRequest(int userId, String roomType) {
        RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto();
        roomCreateReqDto.setUserId(userId);
        roomCreateReqDto.setRoomType(roomType);
        roomCreateReqDto.setTitle("test room by user" + userId);
        return roomCreateReqDto;
    }
}