package my.tableTennisGame.web;

import my.tableTennisGame.domain.user.User;
import my.tableTennisGame.domain.user.UserStatus;
import my.tableTennisGame.dummy.DummyObject;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
class UserControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @DisplayName("유저 전체 조회 API 응답 형식 테스트")
    @Test
    void findUserListApi() throws Exception {
        // given
        int page = 0;
        int size = 10;
        List<User> mockUsers = newMockUsers(1, 10, UserStatus.ACTIVE);
        PageImpl<User> userPage = new PageImpl<>(mockUsers, PageRequest.of(page, size), mockUsers.size());
        UserListRespDto userListRespDto = new UserListRespDto(userPage);

        when(userService.findUserList(any(Pageable.class))).thenReturn(userListRespDto);

        // when & then
        mockMvc.perform(get("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.totalElements").value("10"))
                .andExpect(jsonPath("$.result.totalPages").value("1"))
                .andExpect(jsonPath("$.result.userList.length()").value(mockUsers.size()));
    }
}