package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.room.RoomType;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.repository.RoomRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlayService {

    private final UserRoomService userRoomService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final RoomRepository roomRepository;

    /**
     * 팀 변경
     * 유저(userId)가 현재 해당 방(roomId)에 참가한 상태에서만 팀 변경 가능
     * 현재 방의 상태가 대기(WAIT) 상태일 때만 팀 변경 가능
     * 변경되려는 팀의 인원이 이미 해당 방 정원의 절반과 같다면 팀이 변경되지 않고 201 응답을 반환
     * 유저(userId)가 현재 속한 팀 기준 반대 팀으로 변경 (RED -> BLUE / BLUE -> RED)
     */
    public void changeTeam(int roomId, int userId) {
        UserRoom findUserRoom = userRoomService.findParticipatingUser(roomId, userId)
                .orElseThrow(() -> new WrongRequestException("해당 방 Id와 유저 Id로 참여 정보가 없습니다."));

        // 검증
        Room room = findUserRoom.getRoom();
        if (!room.getStatus().equals(RoomStatus.WAIT))
            throw new WrongRequestException("현재 방의 상태가 대기(WAIT) 상태일 때만 팀을 변경할 수 있습니다.");


    }

    /**
     * 게임 시작
     * 호스트인 유저만 게임을 시작할 수 있다.
     * 현재 방의 상태가 대기(WAIT) 상태일 때만 시작할 수 있다.
     * 방 정원이 방의 타입에 맞게 모두 꽉 찬 상태에서만 게임을 시작할 수 있다.
     * 방의 상태를 진행중(PROGRESS) 상태로 변경한다.
     * 게임시작이 된 방은 1분 뒤 종료(FINISH) 상태로 변경된다.
     */
    public void startGame(int roomId, int userId) {
        UserRoom findUserRoom = userRoomService.findParticipatingUser(roomId, userId)
                .orElseThrow(() -> new WrongRequestException("해당 방 Id와 유저 Id로 참여 정보가 없습니다."));

        // 검증
        Room room = findUserRoom.getRoom();
        if (!room.getHost().equals(findUserRoom.getUser()))
            throw new WrongRequestException("호스트인 유저만 게임을 시작할 수 있습니다.");

        if (!room.getStatus().equals(RoomStatus.WAIT))
            throw new WrongRequestException("현재 방의 상태가 대기(WAIT) 상태일 때만 시작할 수 있습니다.");

        checkCapacity(room);

        play(room);
    }

    /**
     * 방의 상태를 진행중(PROGRESS) 상태로 변경한다.
     * 게임시작이 된 방은 1분 뒤 종료(FINISH) 상태로 변경된다.
     */
    private void play(Room room) {
        // 1. 방의 상태를 진행중(PROGRESS)으로 변경
        room.progress();
        roomRepository.save(room);
        log.info("start play: {}, room status: {}", LocalDateTime.now(), room.getStatus());

        // 2. 1분 후 자동으로 FINISH 상태로 변경하는 비동기 작업 실행
        scheduledRoomFinish(room.getId());
    }

    /**
     * 1분 뒤 방의 상태를 FINISH 로 변경하는 비동기 실행 (트랜잭션 새로 열기)
     */
    @Async //별도의 스레드에서 실행. 비동기 실행으로 인해 트랜잭션이 예상대로 동작하지 않을 수 있음
    @Transactional
    public void scheduledRoomFinish(int roomId) {
        scheduledExecutorService.schedule(() -> {
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new WrongRequestException("방 정보를 찾을 수 없습니다."));
            room.finish();
            roomRepository.save(room);
            log.info("finish play: {}, room status: {}", LocalDateTime.now(), room.getStatus());

            //게임이 종료된 후 방에 있던 모든 유저들은 나가기로 처리
            userRoomService.deleteRoom(roomId);
        }, 1, TimeUnit.MINUTES);
    }

    //방 정원이 방의 타입에 맞게 모두 꽉 찬 상태에서만 게임을 시작할 수 있다.
    private void checkCapacity(Room room) {
        int roomCapacity = room.getRoomType().equals(RoomType.SINGLE) ? 2 : 4;
        List<UserRoom> participants = userRoomService.findParticipants(room.getId());

        if (participants.size() != roomCapacity)
            throw new WrongRequestException("인원이 부족합니다.");
    }
}
