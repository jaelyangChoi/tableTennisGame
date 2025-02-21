package my.tableTennisGame.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.tableTennisGame.service.PlayService;
import my.tableTennisGame.web.dto.play.PlayReqDto;
import my.tableTennisGame.web.dto.play.PlayReqDto.PlayStartReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PlayController.class)
class PlayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlayService playService;

    @DisplayName("게임 시작 API 컨트롤러 테스트")
    @Test
    void startGame() throws Exception {
        // given
        int roomId = 1;
        int userId = 1;
        PlayStartReqDto playStartReqDto = new PlayStartReqDto();
        playStartReqDto.setUserId(userId);

        // when & then
        mockMvc.perform(put("/room/start/" + roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playStartReqDto)))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

}