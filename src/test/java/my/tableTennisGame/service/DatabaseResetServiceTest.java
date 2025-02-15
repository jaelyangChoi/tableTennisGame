package my.tableTennisGame.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseResetServiceTest {

    @Mock
    private EntityManager em;

    @Mock
    private Query mockQuery;

    @InjectMocks
    private DatabaseResetService databaseResetService;

    @DisplayName("데이터베이스 초기화 서비스 테스트")
    @Test
    void databaseReset() {
        //given
        List<String> mockTableNames = List.of("user_tb", "room_tb", "user_room_tb");

        //stubbing
        when(em.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(mockTableNames);

        //when
        databaseResetService.resetDatabase();

        //then
        verify(em).createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE"); //외래 키 비활성화 확인

        for (String tableName : mockTableNames)
            verify(em).createNativeQuery("TRUNCATE TABLE " + tableName); //테이블 삭제 확인

        verify(em).createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE"); //외래 키 활성화 확인

        verifyNoMoreInteractions(em);
    }
}