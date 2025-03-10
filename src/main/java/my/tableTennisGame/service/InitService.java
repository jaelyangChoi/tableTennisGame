package my.tableTennisGame.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import my.tableTennisGame.repository.UserRepository;
import my.tableTennisGame.web.dto.init.FakerApiRespDto;
import my.tableTennisGame.web.dto.init.InitReqDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

import static my.tableTennisGame.web.dto.init.FakerApiRespDto.UserDto;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InitService {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final ExternalApiService externalApiService;


    public void init(InitReqDto reqDto) {
        // 1. 테이블 초기화
        resetDatabase();

        // 2. 전달받은 데이터로 fakerAPI 호출
        List<FakerApiRespDto.UserDto> userDtos = externalApiService.fetchFakers(reqDto.getSeed(), reqDto.getQuantity());

        // 3. 서비스에 필요한 회원 정보 저장
        saveInitData(userDtos);
    }

    /**
     * /init 호출 시, 테이블 초기화
     */
    public void resetDatabase() {
        log.info("Database reset");

        // 외래 키 제약 조건 해제
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        // 테이블 초기화
        for (String tableName : getAllTableNames()) {
            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            log.info("Table " + tableName + " has been reset");
        }

        // 외래 키 제약 조건 활성화
        em.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();

        log.info("Database reset complete");
    }

    /**
     * UserDto의 fakerId를 기준으로 오름차순 정렬 후 저장
     */
    public void saveInitData(List<UserDto> userDtos) {
        log.info("Save init data");
        // 1. id 기준 오름차순 정렬
        List<UserDto> sortedUserDtos = userDtos.stream()
                .sorted(Comparator.comparing(UserDto::getId))
                .toList();

        // 2. DTO 를 엔티티로 변환
        List<User> userEntities = sortedUserDtos.stream()
                .map(this::convertToEntity)
                .toList();

        // 3. 정렬된 엔티티 리스트를 DB에 저장
        userRepository.saveAll(userEntities);
    }

    private User convertToEntity(UserDto userDto) {
        return User.builder()
                .fakerId(userDto.getId())
                .name(userDto.getUsername())
                .email(userDto.getEmail())
                .status(setUserStatus(userDto.getId()))
                .build();
    }

    private UserStatus setUserStatus(int id) {
        if (id <= 30) return UserStatus.ACTIVE;
        if (id <= 60) return UserStatus.WAIT;
        return UserStatus.NON_ACTIVE;
    }

    private List<String> getAllTableNames() {
        return em.createNativeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'")
                .getResultList();
    }

}
