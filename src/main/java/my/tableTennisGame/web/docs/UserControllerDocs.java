package my.tableTennisGame.web.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import my.tableTennisGame.web.dto.init.InitReqDto;
import my.tableTennisGame.web.dto.user.UserRespDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 API", description = "유저 조회")
public interface UserControllerDocs {

    @Operation(summary = "유저 전체 조회 API", description = "모든 유저의 정보를 id 기준 오름차순으로 정렬하여 조회")
    @Parameter(name = "pageable", description = "페이지 정보")
    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponseSchema.class)))
    public my.tableTennisGame.web.dto.ApiResponse<UserRespDto.UserListRespDto> getAllUsers(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable);
}
