package my.tableTennisGame.repository;

import my.tableTennisGame.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
