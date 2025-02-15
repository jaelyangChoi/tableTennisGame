package my.tableTennisGame.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.tableTennisGame.service.init.ExternalApiService;
import my.tableTennisGame.service.init.InitService;
import my.tableTennisGame.web.dto.init.InitReqDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SystemController.class)
class SystemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InitService initService;

    @MockitoBean
    private ExternalApiService externalApiService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("헬스 체크 성공 응답 테스트")
    @Test
    void healthCheck_Success() throws Exception {
        mockMvc.perform(get("/health")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @DisplayName("초기화 성공 응답 테스트")
    @Test
    void initSuccess() throws Exception {
        //given
        InitReqDto initReqDto = new InitReqDto();
        initReqDto.setSeed(30);
        initReqDto.setQuantity(40);


        //when & then
        mockMvc.perform(post("/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initReqDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

}