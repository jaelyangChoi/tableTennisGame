package my.tableTennisGame.service;

import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.Team;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    private final int userId = 1;
    private final int roomId = 1;

    @DisplayName("게임 시작 서비스 테스트_성공")
    @Test
    void startGame_success() {
        // given
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
        int participantId = 2;
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

    @DisplayName("팀 변경 테스트_단식 - 성공")
    @Test
    void changeTeam_single_success() {
        // given
        Room testRoom = newMockRoom(roomId, null, "SINGLE", "WAIT");
        UserRoom testUserRoom = UserRoom.builder().room(testRoom).team(Team.RED).build();
        when(userRoomService.findParticipatingUser(roomId, userId)).thenReturn(Optional.ofNullable(testUserRoom));
        assert testUserRoom != null;
        when(userRoomService.findParticipants(roomId)).thenReturn(List.of(testUserRoom));

        // when
        playService.changeTeam(roomId, userId);

        // then
        assertThat(testUserRoom.getTeam()).isEqualTo(Team.BLUE);
    }

    @DisplayName("팀 변경 테스트_단식 - 실패")
    @Test
    void changeTeam_single_fail() {
        // given
        Room testRoom = newMockRoom(roomId, null, "SINGLE", "WAIT");
        UserRoom testUserRoom = UserRoom.builder().room(testRoom).team(Team.RED).build();
        UserRoom testUserRoom2 = UserRoom.builder().room(testRoom).team(Team.BLUE).build();
        when(userRoomService.findParticipatingUser(roomId, userId)).thenReturn(Optional.ofNullable(testUserRoom));
        assert testUserRoom != null;
        when(userRoomService.findParticipants(roomId)).thenReturn(List.of(testUserRoom, testUserRoom2));

        // when & then
        WrongRequestException exception = assertThrows(WrongRequestException.class, () -> playService.changeTeam(roomId, userId));
        assertEquals("변경하려는 팀의 인원이 모두 찼습니다.", exception.getMessage());


    }

    @DisplayName("팀 변경 테스트 - 복식")
    @ParameterizedTest(name = "{index} => 기존 팀 구성: {2}, 변경 결과: RED->{1}")
    @MethodSource("provideTeamChangeTestCases")
    void changeTeam_double(List<UserRoom> existingTeams, Team expectedTeam, String teamNames) {
        // given
        Room testRoom = newMockRoom(roomId, null, "DOUBLE", "WAIT");
        UserRoom testUserRoom = UserRoom.builder().room(testRoom).team(Team.RED).build();
        when(userRoomService.findParticipatingUser(roomId, userId)).thenReturn(Optional.ofNullable(testUserRoom));
        when(userRoomService.findParticipants(roomId)).thenReturn(existingTeams);

        // when
        playService.changeTeam(roomId, userId);

        // then
        assert testUserRoom != null;
        assertThat(testUserRoom.getTeam()).isEqualTo(expectedTeam);
    }

    /**
     * 팀 변경 테스트 데이터 제공 - 기존 팀: RED
     */
    private static Stream<Arguments> provideTeamChangeTestCases() {
        return Stream.of(
                createTestCase(List.of(
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.BLUE).build()), Team.BLUE),
                createTestCase(List.of(
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.BLUE).build()), Team.BLUE)
        );
    }

    private static Arguments createTestCase(List<UserRoom> teamList, Team expectedTeam) {
        String teamNames = teamList.stream()
                .map(userRoom -> userRoom.getTeam().name())
                .collect(Collectors.joining(", "));

        return Arguments.of(teamList, expectedTeam, teamNames);
    }
}