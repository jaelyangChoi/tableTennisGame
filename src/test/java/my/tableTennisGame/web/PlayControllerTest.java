package my.tableTennisGame.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.tableTennisGame.service.PlayService;
import my.tableTennisGame.web.dto.play.PlayReqDto.PlayStartReqDto;
import my.tableTennisGame.web.dto.play.PlayReqDto.TeamChangeReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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

    private final int roomId = 1;
    private final int userId = 1;

    @DisplayName("게임 시작 API 컨트롤러 테스트")
    @Test
    void startGame() throws Exception {
        // given
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

    @DisplayName("팀 변경 API 컨트롤러 테스트")
    @Test
    void changeTeam() throws Exception {
        // given
        TeamChangeReqDto teamChangeReqDto = new TeamChangeReqDto();
        teamChangeReqDto.setUserId(userId);

        // when & then
        mockMvc.perform(put("/team/" + roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamChangeReqDto)))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

}