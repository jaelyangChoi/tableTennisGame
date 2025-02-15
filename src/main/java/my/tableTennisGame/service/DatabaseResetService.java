package my.tableTennisGame.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DatabaseResetService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
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

    private List<String> getAllTableNames() {
        return em.createNativeQuery(
                "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'"
        ).getResultList();
    }
}
