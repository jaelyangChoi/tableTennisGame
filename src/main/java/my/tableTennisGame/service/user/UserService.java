package my.tableTennisGame.service.user;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.repository.UserRepository;
import my.tableTennisGame.web.dto.user.UserRespDto.UserListRespDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserListRespDto findUserList(Pageable pageable) {

        Page<User> userPages = userRepository.findAll(pageable);

        return new UserListRespDto(userPages);
    }
}
