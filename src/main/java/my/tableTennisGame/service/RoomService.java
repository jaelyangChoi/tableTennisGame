package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.room.RoomType;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.Team;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.repository.RoomRepository;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import my.tableTennisGame.web.dto.room.RoomRespDto.RoomDetailDto;
import my.tableTennisGame.web.dto.room.RoomRespDto.RoomListRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserService userService;
    private final UserRoomService userRoomService;


    @Transactional
    public void createRoom(RoomCreateReqDto roomCreateReqDto) {
        // 1. 유저 유효성 검사 - 존재 여부, 활성 상태 여부, 기 참여 여부
        User host = userService.getValidUser(roomCreateReqDto.getUserId());

        // 2. 방 생성
        Room room = roomRepository.save(roomCreateReqDto.toEntity(host));

        //3. 사용자-방 참여 매핑 (방 생성자가 방에 자동 참여)
        userRoomService.addUserToRoom(host, room, Team.RED); //방 생성 시 host 는 RED 팀 배정
    }

    public RoomListRespDto getRoomList(Pageable pageable) {
        Page<Room> roomPage = roomRepository.findAll(pageable);
        return new RoomListRespDto(roomPage);
    }

    public RoomDetailDto getRoomDetails(int roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new WrongRequestException("존재하지 않는 방입니다."));
        return new RoomDetailDto(room);
    }

    /**
     * 방 참가 조건
     * 대기(WAIT) 상태인 방
     * 유저(userId)가 활성(ACTIVE) 상태
     * 유저(userId)가 현재 참여한 방이 없을때
     * 참가하고자 하는 방(roomId)의 정원이 미달일 때
     */
    @Transactional
    public void participateToRoom(int roomId, int userId) {
        //유저 검증
        User user = userService.getValidUser(userId);//존재, 활성 상태, 참여 중인 방x

        // 방 검증
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new WrongRequestException("존재하지 않는 방입니다."));
        if (!room.getStatus().equals(RoomStatus.WAIT))
            throw new WrongRequestException("대기(WAIT) 상태인 방에만 참여할 수 있습니다.");

        //정원 검증 및 팀 배정
        Team assignedTeam = assignedTeam(room);

        // 팀 등록
        userRoomService.addUserToRoom(user, room, assignedTeam);
    }

    /**
     * 정원 검증 및 팀 배정
     * - 한 쪽 팀에 인원이 모두 찬 경우, 반대팀으로 배정
     * - 양쪽 팀에 모두 자리가 있는 경우, RED 팀에 먼저 배정
     */
    private Team assignedTeam(Room room) {
        int roomCapacity = room.getRoomType().equals(RoomType.SINGLE) ? 2 : 4;
        int teamCapacity = roomCapacity / 2;

        List<UserRoom> participants = userRoomService.findParticipants(room.getId());

        if (participants.size() >= roomCapacity)
            throw new WrongRequestException("참가하고자 하는 방의 인원이 모두 찼습니다.");

        //한 쪽 팀에 인원이 모두 찬 경우, BLUE 팀으로 배정
        return participants.size() >= teamCapacity ? Team.BLUE : Team.RED;
    }
}
