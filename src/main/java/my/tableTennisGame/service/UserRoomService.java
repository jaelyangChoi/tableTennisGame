package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.Team;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.repository.UserRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;


    @Transactional
    public UserRoom addUserToRoom(User user, Room room, Team team) {
        UserRoom userRoom = UserRoom.builder()
                .room(room)
                .user(user)
                .team(team)
                .build();

        return userRoomRepository.save(userRoom);
    }

    public List<UserRoom> findParticipants(Integer roomId) {
        return userRoomRepository.findByRoomId(roomId);
    }
}
