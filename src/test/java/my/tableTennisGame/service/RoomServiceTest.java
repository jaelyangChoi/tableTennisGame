package my.tableTennisGame.service;

import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.Team;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.repository.RoomRepository;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
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
        String roomStatus = "WAIT";
        RoomCreateReqDto roomCreateReqDto = createRequest(userId, roomType);
        User mockHost = newMockUser(userId);
        Room mockRoom = newMockRoom(1, mockHost, roomType, roomStatus);

        when(userService.getValidUser(userId)).thenReturn(mockHost);
        when(roomRepository.save(any())).thenReturn(mockRoom);

        // when
        roomService.createRoom(roomCreateReqDto);

        verify(userService).getValidUser(userId);
        verify(roomRepository).save(any());
        verify(userRoomService).addUserToRoom(mockHost, mockRoom, Team.RED);
        verifyNoMoreInteractions(userService, roomRepository, userRoomService);
    }

    private RoomCreateReqDto createRequest(int userId, String roomType) {
        RoomCreateReqDto roomCreateReqDto = new RoomCreateReqDto();
        roomCreateReqDto.setUserId(userId);
        roomCreateReqDto.setRoomType(roomType);
        roomCreateReqDto.setTitle("test room by user" + userId);
        return roomCreateReqDto;
    }

    @DisplayName("방 참가 서비스 테스트")
    @Test
    void participateToRoom() {
        // given
        int userId = 1;
        int roomId = 1;

        User user = newMockUser(userId);
        Room room = newMockRoom(roomId, user, "SINGLE", "WAIT");

        when(userService.getValidUser(userId)).thenReturn(user);
        when(roomRepository.findById(roomId)).thenReturn(Optional.ofNullable(room));
        when(userRoomService.findParticipants(roomId)).thenReturn(List.of(UserRoom.builder().team(Team.RED).build()));

        // when
        roomService.participateToRoom(roomId, userId);

        //then
        then(userRoomService).should().addUserToRoom(user, room, Team.BLUE); //Blue 팀에 배경되어야 함
    }

    @DisplayName("방 나가기 서비스 테스트_일반 유저")
    @Test
    void out() {
        // given
        int hostId = 1;
        int userId = 2;
        int roomId = 1;

        User host = newMockUser(hostId);
        User user = newMockUser(userId);

        Room room = newMockRoom(roomId, host, "DOUBLE", "WAIT");
        UserRoom userRoomHost = newMockUserRoom(1, host, room);
        UserRoom userRoom = newMockUserRoom(2, user, room);

        when(userRoomService.findParticipatingUser(roomId, userId)).thenReturn(Optional.ofNullable(userRoom));

        // when
        roomService.out(roomId, userId);

        // then
        verify(userRoomService, times(1)).delete(userRoom);
        verify(userRoomService, never()).deleteRoom(roomId);
    }

    @DisplayName("방 나가기 서비스 테스트_호스트가 나가면 참여 인원이 모두 나가고 방 상태가 FINISH 가 된다.")
    @Test
    void out_host() {
        // given
        int hostId = 1;
        int userId = 2;
        int roomId = 1;

        User host = newMockUser(hostId);
        User user = newMockUser(userId);

        Room room = newMockRoom(roomId, host, "DOUBLE", "WAIT");
        UserRoom userRoomHost = newMockUserRoom(1, host, room);
        UserRoom userRoom = newMockUserRoom(2, user, room);

        when(userRoomService.findParticipatingUser(roomId, hostId)).thenReturn(Optional.ofNullable(userRoomHost));

        // when
        roomService.out(roomId, hostId);

        // then
        verify(userRoomService, never()).delete(userRoom);
        verify(userRoomService, times(1)).deleteRoom(roomId);
        assertThat(room.getStatus()).isEqualTo(RoomStatus.FINISH);
    }

    @DisplayName("팀 할당 테스트 - RED 팀 먼저 할당")
    @Test
    void assignedTeam() {
        // given
        Room testRoom = newMockRoom(1, null, "DOUBLE", "WAIT");
        List<UserRoom> urList = List.of(
                UserRoom.builder().team(Team.RED).build(),
                UserRoom.builder().team(Team.BLUE).build());

        when(userRoomService.findParticipants(testRoom.getId())).thenReturn(urList);

        // when
        Team resultTeam = roomService.assignedTeam(testRoom);
        assertThat(resultTeam).isEqualTo(Team.RED);
    }

    @DisplayName("팀 할당 테스트 - RED 멤버가 나간 경우 RED 에 다시 할당")
    @Test
    void assignedTeam_blue() {
        // given
        Room testRoom = newMockRoom(1, null, "DOUBLE", "WAIT");
        List<UserRoom> urList = List.of(
                UserRoom.builder().team(Team.RED).build(),
                UserRoom.builder().team(Team.RED).build(),
                UserRoom.builder().team(Team.BLUE).build());

        when(userRoomService.findParticipants(testRoom.getId())).thenReturn(urList);

        // when
        Team resultTeam = roomService.assignedTeam(testRoom);
        assertThat(resultTeam).isEqualTo(Team.BLUE);
    }

    @DisplayName("팀 할당 테스트 - 단식")
    @Test
    void assignedTeam_single() {
        // given
        Room testRoom = newMockRoom(1, null, "SINGLE", "WAIT");
        List<UserRoom> urList = List.of(UserRoom.builder().team(Team.RED).build());

        when(userRoomService.findParticipants(testRoom.getId())).thenReturn(urList);

        // when
        Team resultTeam = roomService.assignedTeam(testRoom);
        assertThat(resultTeam).isEqualTo(Team.BLUE);
    }

    @DisplayName("팀 할당 테스트 - 복식")
    @ParameterizedTest(name = "{index} => 기존 팀: {2}, 예상 할당 팀: {1}")
    @MethodSource("provideTeamAssignmentTestCases")
    void assignedTeam_Double(List<UserRoom> existingTeams, Team expectedTeam, String teamNames) {
        // given
        Room testRoom = newMockRoom(1, null, "DOUBLE", "WAIT");
        when(userRoomService.findParticipants(testRoom.getId())).thenReturn(existingTeams);

        // when
        Team resultTeam = roomService.assignedTeam(testRoom);

        // then
        assertThat(resultTeam).isEqualTo(expectedTeam);
    }

    /**
     * 팀 할당 테스트 데이터 제공
     */
    private static Stream<Arguments> provideTeamAssignmentTestCases() {
        return Stream.of(
                createTestCase(List.of(
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.BLUE).build()), Team.RED),
                createTestCase(List.of(
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.BLUE).build()), Team.BLUE),
                createTestCase(List.of(
                        UserRoom.builder().team(Team.RED).build(),
                        UserRoom.builder().team(Team.BLUE).build(),
                        UserRoom.builder().team(Team.BLUE).build()), Team.RED)
        );
    }

    private static Arguments createTestCase(List<UserRoom> teamList, Team expectedTeam) {
        String teamNames = teamList.stream()
                .map(userRoom -> userRoom.getTeam().name())
                .collect(Collectors.joining(", "));

        return Arguments.of(teamList, expectedTeam, teamNames);
    }
}