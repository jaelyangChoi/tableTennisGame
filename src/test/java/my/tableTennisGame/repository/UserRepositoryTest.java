package my.tableTennisGame.repository;

import my.tableTennisGame.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing //테스트에서 auditing 기능 활성화
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @DisplayName("회원 정보 저장시 생성일, 수정일 자동 생성 테스트")
    @Test
    void auditingTest() {
        //given
        User user = User.builder()
                .fakerId(1)
                .build();

        //when
        User savedUser = userRepository.save(user);
        userRepository.flush(); //DB 반영

        //then
        LocalDateTime createdAt = savedUser.getCreatedAt();
        LocalDateTime updatedAt = savedUser.getUpdatedAt();

        assertThat(createdAt).isNotNull();
        assertThat(updatedAt).isNotNull();
        assertThat(createdAt).isEqualTo(updatedAt);
    }

}