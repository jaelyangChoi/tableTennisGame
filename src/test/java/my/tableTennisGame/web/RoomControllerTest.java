package my.tableTennisGame.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import my.tableTennisGame.web.dto.room.RoomRespDto;
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

import static my.tableTennisGame.web.dto.room.RoomRespDto.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RoomController.class)
class RoomControllerTest extends DummyObject {

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

    @DisplayName("방 전체 목록 조회 API 컨트롤러 테스트")
    @Test
    void getAllRooms() throws Exception {
        // given
        int page = 0;
        int size = 10;
        List<Room> mockRooms = newMockRooms(1, 10, "SINGLE");
        PageImpl<Room> roomPage = new PageImpl<>(mockRooms, PageRequest.of(page, size), mockRooms.size());
        RoomListRespDto roomListRespDto = new RoomListRespDto(roomPage);

        when(roomService.findRoomList(any(Pageable.class))).thenReturn(roomListRespDto);

        // when & then
        mockMvc.perform(get("/room")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.totalElements").value(mockRooms.size()))
                .andExpect(jsonPath("$.result.totalPages").value(mockRooms.size()/size))
                .andExpect(jsonPath("$.result.roomList.length()").value(mockRooms.size()));
    }

}