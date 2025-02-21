package my.tableTennisGame.domain.userRoom;

import jakarta.persistence.*;
import lombok.*;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.domain.user.User;
/**
 * 방 - 유저 참여 정보
 */
@Entity
@Table(name = "user_room_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class UserRoom {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Team team;

    public void changeTeam(Team oppositeTeam) {
        team = oppositeTeam;
    }
}