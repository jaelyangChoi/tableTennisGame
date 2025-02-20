package my.tableTennisGame.repository;

import my.tableTennisGame.domain.userRoom.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {
    @Query("select us from UserRoom us join fetch us.room r where us.user.id = :userId")
    List<UserRoom> findParticipatingRooms(int userId);
}
