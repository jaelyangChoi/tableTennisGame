package my.tableTennisGame.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import my.tableTennisGame.common.exception.WrongRequestException;
import my.tableTennisGame.domain.room.Room;
import my.tableTennisGame.dummy.DummyObject;
import my.tableTennisGame.service.RoomService;
import my.tableTennisGame.web.dto.room.RoomReqDto;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomAttentionReqDto;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomCreateReqDto;
import my.tableTennisGame.web.dto.room.RoomReqDto.RoomOutReqDto;
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

import static my.tableTennisGame.web.dto.room.RoomRespDto.RoomDetailDto;
import static my.tableTennisGame.web.dto.room.RoomRespDto.RoomListRespDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

        when(roomService.getRoomList(any(Pageable.class))).thenReturn(roomListRespDto);

        // when & then
        mockMvc.perform(get("/room")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.totalElements").value(mockRooms.size()))
                .andExpect(jsonPath("$.result.totalPages").value(mockRooms.size() / size))
                .andExpect(jsonPath("$.result.roomList.length()").value(mockRooms.size()));
    }

    @DisplayName("방 상세 조회 테스트_성공")
    @Test
    void getRoomDetail_success() throws Exception {
        // given
        int roomId = 1;
        String roomType = "SINGLE";
        String roomStatus = "WAIT";
        RoomDetailDto roomDetailDto = new RoomDetailDto(newMockRoom(roomId, newMockUser(1), roomType, roomStatus));

        when(roomService.getRoomDetails(roomId)).thenReturn(roomDetailDto);

        // when & then
        mockMvc.perform(get("/room/" + roomId))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result.id").value(roomId))
                .andExpect(jsonPath("$.result.roomType").value(roomType));
    }

    @DisplayName("방 상세 조회 테스트_실패")
    @Test
    void getRoomDetail_fail() throws Exception {
        // given
        when(roomService.getRoomDetails(anyInt())).thenThrow(WrongRequestException.class);

        // when & then
        mockMvc.perform(get("/room/" + 1))
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("불가능한 요청입니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @DisplayName("방 참가 API 컨트롤러 테스트")
    @Test
    void participateToRoom() throws Exception {
        int roomId = 1;
        int userId = 1;
        RoomAttentionReqDto roomAttentionReqDto = new RoomAttentionReqDto();
        roomAttentionReqDto.setUserId(userId);

        // when & then
        mockMvc.perform(post("/room/attention/" + roomId)
                        .content(objectMapper.writeValueAsString(roomAttentionReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @DisplayName("방 나가기 API 컨트롤러 테스트")
    @Test
    void outTheRoom() throws Exception {
        int roomId = 1;
        int userId = 1;
        RoomOutReqDto roomOutReqDto = new RoomOutReqDto();
        roomOutReqDto.setUserId(userId);

        // when & then
        mockMvc.perform(post("/room/out/" + roomId)
                        .content(objectMapper.writeValueAsString(roomOutReqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.message").value("API 요청이 성공했습니다."))
                .andExpect(jsonPath("$.result").doesNotExist());
    }


}