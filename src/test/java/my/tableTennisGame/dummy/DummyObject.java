package my.tableTennisGame.dummy;

import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

//상속 받은 클래스에서 해당 메소드 사용
public class DummyObject {

    protected List<User> newMockUsers(int fromId, int toId, UserStatus userStatus) {
        return IntStream.range(fromId, toId + 1)
                .mapToObj(i -> {
                    User user = User.builder()
                            .id(i)
                            .fakerId(i)
                            .name("test" + i)
                            .status(userStatus)
                            .build();
                    ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
                    ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());
                    return user;
                })
                .toList();
    }
}
