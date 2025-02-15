package my.tableTennisGame.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import my.tableTennisGame.domain.BaseTimeEntity;

@Entity
@Table(name="user_tb")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer fakerId;

    private String name;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Email
    private String email;

}