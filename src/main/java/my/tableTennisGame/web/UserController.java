package my.tableTennisGame.web;

import lombok.RequiredArgsConstructor;
import my.tableTennisGame.service.UserService;
import my.tableTennisGame.web.docs.UserControllerDocs;
import my.tableTennisGame.web.dto.ApiResponse;
import my.tableTennisGame.web.dto.user.UserRespDto.UserListRespDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserService userService;

    /**
     * 3. 유저 전체 조회 API
     * - 페이징 처리를 위한 size, page 값을 RequestParameter로 받음
     * - 모든 회원 정보를 응답(id 기준 오름차순으로 정렬해서 반환)
     */
    @GetMapping("/user")
    public ApiResponse<UserListRespDto> getAllUsers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        UserListRespDto userListRespDto = userService.findUserList(pageable);
        return ApiResponse.success(userListRespDto);
    }
}
