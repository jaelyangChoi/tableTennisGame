package my.tableTennisGame.domain.room;

import jakarta.persistence.*;
import lombok.*;
import my.tableTennisGame.domain.BaseTimeEntity;
import my.tableTennisGame.domain.user.User;

@Entity
@Table(name="room_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Room extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    private RoomStatus status;

}
