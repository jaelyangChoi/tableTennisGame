package my.tableTennisGame.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RoomService roomService;

    @DisplayName("방 생성 API 응답 테스트")
    @Test
    void createRoom() throws Exception {
        // given
        RoomCreateReqDto requestBody = new RoomCreateReqDto();
        requestBody.setRoomType("SINGLE");
        requestBody.setTitle("Single Room");
        requestBody.setUserId(1);

        // when & then
        mockMvc.perform(post("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());

    }

}