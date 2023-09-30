package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pickup_shuttle.pickup._core.utils.ApiResult;
import pickup_shuttle.pickup._core.utils.ApiUtils;
import pickup_shuttle.pickup.domain.board.dto.response.BoardListResponseDTO;

@RestController
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping("/articles")
    public ApiResult<?> getBoardList(
            @RequestParam(value = "lastBoardId", required = false) Long lastBoardId,
            @RequestParam(value = "limit",defaultValue = "10") int size) {
        Slice<BoardListResponseDTO> responseDTOSlice = boardService.boardList(lastBoardId, size);
        return ApiUtils.success(responseDTOSlice);
    }
}
