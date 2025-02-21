package my.tableTennisGame.repository;

import my.tableTennisGame.domain.userRoom.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {
    @Query("select ur from UserRoom ur join fetch ur.room r where ur.user.id = :userId")
    List<UserRoom> findParticipatingRooms(int userId);

    List<UserRoom> findByRoomId(Integer roomId);

    @Query("select ur from UserRoom ur join fetch ur.room where ur.user.id = :userId and ur.room.id = :roomId")
    Optional<UserRoom> findByRoomIdAndUserId(int roomId, int userId);

    @Modifying //벌크 연산 최적화하지 않으면 select 후 n번 delete
    @Query("delete from UserRoom ur where ur.room.id = :roomId")
    void deleteByRoomId(int roomId);
}
