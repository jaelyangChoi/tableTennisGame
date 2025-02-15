package my.tableTennisGame.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import my.tableTennisGame.repository.UserRepository;
import my.tableTennisGame.service.init.InitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static my.tableTennisGame.web.dto.common.FakerApiRespDto.UserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private InitService initService;

    @Mock
    private UserRepository userRepository;

    @DisplayName("데이터베이스 초기화 서비스 테스트")
    @Test
    void databaseReset() {
        //given
        List<String> mockTableNames = List.of("user_tb", "room_tb", "user_room_tb");

        //stubbing
        when(em.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockTableNames);

        //when
        initService.resetDatabase();

        //then
        verify(em).createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE"); //외래 키 비활성화 확인

        for (String tableName : mockTableNames)
            verify(em).createNativeQuery("TRUNCATE TABLE " + tableName + " RESTART IDENTITY"); //테이블 삭제 확인

        verify(em).createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE"); //외래 키 활성화 확인

        verifyNoMoreInteractions(em);
    }

    @DisplayName("fakerDto를 오름차순으로 정렬하여 저장 & 상태 값 생성 확인")
    @Test
    void sortAndSave() {
        // given: id 순서가 섞인 UserDto 리스트 생성
        int firstId = 30;
        int secondId = 60;
        int thirdId = 61;
        List<UserDto> unsortedDtos = getUnsortedDtos(firstId, secondId, thirdId);

        // when
        initService.saveInitData(unsortedDtos);

        // then: repository.saveAll()에 전달된 인자를 캡처하여 정렬 여부 검증
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<User>> captor = ArgumentCaptor.forClass(List.class);
        verify(userRepository).saveAll(captor.capture());
        List<User> savedUsers = captor.getValue();

        // 저장된 User 리스트가 id 기준 오름차순으로 정렬되었는지 검증
        assertEquals(firstId, savedUsers.get(0).getFakerId());
        assertEquals(secondId, savedUsers.get(1).getFakerId());
        assertEquals(thirdId, savedUsers.get(2).getFakerId());

        // 상태 값 확인
        assertEquals(UserStatus.ACTIVE, savedUsers.get(0).getStatus());
        assertEquals(UserStatus.WAIT, savedUsers.get(1).getStatus());
        assertEquals(UserStatus.NON_ACTIVE, savedUsers.get(2).getStatus());
    }

    private List<UserDto> getUnsortedDtos(int firstId, int secondId, int thirdId) {
        UserDto dto3 = new UserDto();
        dto3.setId(firstId);

        UserDto dto1 = new UserDto();
        dto1.setId(secondId);

        UserDto dto2 = new UserDto();
        dto2.setId(thirdId);

        List<UserDto> unsortedDtos = Arrays.asList(dto3, dto1, dto2);
        return unsortedDtos;
    }

}