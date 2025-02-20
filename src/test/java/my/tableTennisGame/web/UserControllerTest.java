package my.tableTennisGame.web;

import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import my.tableTennisGame.service.UserService;
import my.tableTennisGame.web.dto.user.UserRespDto.UserListRespDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;


    @DisplayName("유저 전체 조회 API 응답 형식 테스트")
    @Test
    void findUserListApi() throws Exception {
        // given
        PageImpl<User> userPage = new PageImpl<>(generateTestUsers(), PageRequest.of(0, 10), 10);
        UserListRespDto userListRespDto = new UserListRespDto(userPage);

        when(userService.findUserList(any(Pageable.class))).thenReturn(userListRespDto);

        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.totalElements").value("10"))
                .andExpect(jsonPath("$.result.totalPages").value("1"))
                .andExpect(jsonPath("$.result.userList.length()").value("10"));
    }

    private List<User> generateTestUsers() {
        return IntStream.range(0, 10)
                .mapToObj(i -> {
                    User user = User.builder()
                            .id(1)
                            .fakerId(1)
                            .name("test" + i)
                            .status(UserStatus.ACTIVE)
                            .build();
                    ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
                    ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());
                    return user;
                })
                .toList();
    }
}