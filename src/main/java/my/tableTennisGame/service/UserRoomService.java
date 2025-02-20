package my.tableTennisGame.service;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.userRoom.Team;
import my.tableTennisGame.domain.userRoom.UserRoom;
import my.tableTennisGame.repository.UserRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRoomService {

    private final UserRoomRepository userRoomRepository;


    @Transactional
    public UserRoom addUserToRoom(User user, Room room) {
        UserRoom userRoom = UserRoom.builder()
                .room(room)
                .user(user)
                .team(assignTeam(room))
                .build();

        return userRoomRepository.save(userRoom);
    }

    private Team assignTeam(Room room) {
        return Team.RED;
    }
}
