package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.RoomStatus;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.repository.UserRepository;
import my.tableTennisGame.repository.UserRoomRepository;
import my.tableTennisGame.web.dto.user.UserRespDto.UserListRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;

    public UserListRespDto findUserList(Pageable pageable) {

        Page<User> userPages = userRepository.findAll(pageable);

        return new UserListRespDto(userPages);
    }

    /**
     * 유저가 활성(ACTIVE) 상태이고, 현재 참여한 방이 없을때만 방 생성 가능
     */
    public User validateHost(int userId) {
        Optional<User> findUser = userRepository.findById(userId);

        if (findUser.isEmpty())
            throw new WrongRequestException("존재하지 않는 회원입니다.");

        User user = findUser.get();
        if (!user.getStatus().equals(UserStatus.ACTIVE))
            throw new WrongRequestException("유저가 활성(ACTIVE) 상태일 때 방을 생성할 수 있습니다.");

        // 현재 참여한 방이 있다면, 방을 생성할 수 없음
        List<UserRoom> userRooms = userRoomRepository.findParticipatingRooms(userId);
        userRooms.forEach(userRoom -> {
            if (userRoom.getRoom().getStatus() != RoomStatus.FINISH)
                throw new WrongRequestException("유저가 이미 참여 중인 방이 있다면, 참여할 수 없습니다.");
        });

        return user;
    }
}
