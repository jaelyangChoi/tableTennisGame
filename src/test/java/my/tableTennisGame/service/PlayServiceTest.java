package my.tableTennisGame.service;

import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayServiceTest extends DummyObject {

    @Mock
    private UserRoomService userRoomService;

    @Mock
    RoomRepository roomRepository;

    @Mock
    private ScheduledExecutorService scheduledExecutorService;

    @InjectMocks
    private PlayService playService;


    @DisplayName("게임 시작 서비스 테스트_성공")
    @Test
    void startGame_success() {
        // given
        int roomId = 1;
        int userId = 1;
        User testHost = newMockUser(userId);
        Room testRoom = newMockRoom(roomId, testHost, "DOUBLE", "WAIT");
        UserRoom testUserRoom = newMockUserRoom(1, testHost, testRoom);

        when(userRoomService.findParticipatingUser(anyInt(), anyInt())).thenReturn(Optional.ofNullable(testUserRoom));
        when(userRoomService.findParticipants(roomId)).thenReturn(newMockUserRooms(1, 4));

        // when
        playService.startGame(roomId, userId);

        // then
        assertEquals(RoomStatus.PROGRESS, testRoom.getStatus());
        verify(roomRepository, times(1)).save(any(Room.class)); //상태 저장 검증
        verify(scheduledExecutorService, times(1)).schedule(any(Runnable.class), eq(1L), eq(TimeUnit.MINUTES)); //1분 후 종료 검증
    }

    @DisplayName("게임 시작 서비스 테스트_실패 - 호스트가 아닌 유저")
    @Test
    void startGame_Fail_NotHost() {
        // given
        int roomId = 1;
        int participantId = 2;
        int userId = 1;
        User testHost = newMockUser(userId);
        User participant = newMockUser(participantId);
        Room testRoom = newMockRoom(roomId, testHost, "DOUBLE", "WAIT");
        UserRoom testUserRoom = newMockUserRoom(1, participant, testRoom);

        when(userRoomService.findParticipatingUser(anyInt(), anyInt())).thenReturn(Optional.ofNullable(testUserRoom));

        // when & then
        WrongRequestException exception = assertThrows(WrongRequestException.class, () -> playService.startGame(roomId, participantId));
        assertEquals("호스트인 유저만 게임을 시작할 수 있습니다.", exception.getMessage());
    }

    @DisplayName("게임 시작 서비스 테스트_실패 - 방 상태가 WAIT 가 아님")
    @Test
    void startGame_Fail_InvalidRoomStatus() {
        // given
        int roomId = 1;
        int userId = 1;
        User testHost = newMockUser(userId);
        Room testRoom = newMockRoom(roomId, testHost, "DOUBLE", "FINISH");
        UserRoom testUserRoom = newMockUserRoom(1, testHost, testRoom);

        when(userRoomService.findParticipatingUser(anyInt(), anyInt())).thenReturn(Optional.ofNullable(testUserRoom));

        // when & then
        WrongRequestException exception = assertThrows(WrongRequestException.class, () -> playService.startGame(roomId, userId));
        assertEquals("현재 방의 상태가 대기(WAIT) 상태일 때만 시작할 수 있습니다.", exception.getMessage());
    }

    @DisplayName("게임 시작 서비스 테스트_실패 - 방 인원 부족")
    @Test
    void startGame_Fail_NotEnoughParticipants() {
        // given
        int roomId = 1;
        int userId = 1;
        User testHost = newMockUser(userId);
        Room testRoom = newMockRoom(roomId, testHost, "DOUBLE", "WAIT");
        UserRoom testUserRoom = newMockUserRoom(1, testHost, testRoom);

        when(userRoomService.findParticipatingUser(anyInt(), anyInt())).thenReturn(Optional.ofNullable(testUserRoom));
        when(userRoomService.findParticipants(roomId)).thenReturn(newMockUserRooms(1, 3));

        // when & then
        WrongRequestException exception = assertThrows(WrongRequestException.class, () -> playService.startGame(roomId, userId));
        assertEquals("인원이 부족합니다.", exception.getMessage());
    }

    @DisplayName("1분 후 게임 종료 테스트")
    @Test
    void scheduledRoomFinish_Success() {
        // given
        int roomId = 1;
        Room testRoom = newMockRoom(roomId, newMockUser(1), "DOUBLE", "WAIT");

        // `scheduledExecutorService.schedule()`이 실행될 때 즉시 Runnable 을 실행하도록 설정
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0); // 첫 번째 인자 (Runnable) 가져오기
            runnable.run(); // 즉시 실행
            return null;
        }).when(scheduledExecutorService).schedule(any(Runnable.class), eq(1L), eq(TimeUnit.MINUTES));

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));

        // when
        playService.scheduledRoomFinish(roomId);

        // Then: 1분 후 실행되는 작업 검증
        verify(scheduledExecutorService, times(1)).schedule(any(Runnable.class), eq(1L), eq(TimeUnit.MINUTES));

        // 상태 변경 검증
        verify(roomRepository, times(1)).findById(roomId);
        assertEquals(RoomStatus.FINISH, testRoom.getStatus()); // 1분 후 상태가 FINISH 로 변경되었는지 검증

        // 삭제 처리 검증
        verify(userRoomService, times(1)).deleteRoom(1);
    }
}