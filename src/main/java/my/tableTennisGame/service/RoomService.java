package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.repository.RoomRepository;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = userService.validateHost(roomCreateReqDto.getUserId());

        // 2. 방 생성
        Room room = roomRepository.save(roomCreateReqDto.toEntity(user));

        //3. 사용자-방 참여 매핑 (방 생성자가 방에 자동 참여)
        userRoomService.addUserToRoom(user, room);
    }
}
