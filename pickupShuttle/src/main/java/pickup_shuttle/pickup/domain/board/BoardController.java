package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pickup_shuttle.pickup._core.utils.ApiUtils;
import pickup_shuttle.pickup.domain.board.dto.response.BoardDetailAfterRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardDetailBeforeRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardListRpDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("articles")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<?> getBoardList(
            @RequestParam(value = "offset" +
                    "", required = false) Long lastBoardId,
            @RequestParam(value = "limit",defaultValue = "10") int size) {
        Slice<BoardListRpDTO> responseDTOSlice = boardService.boardList(lastBoardId, size);
        return ResponseEntity.ok(ApiUtils.success(responseDTOSlice));
    }

    @GetMapping("/before/{boardId}")
    public ResponseEntity<?> beforeBoardDetail(@PathVariable("boardId") Long boardId) {
        BoardDetailBeforeRpDTO responseDTO = boardService.boardDetailBefore(boardId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @GetMapping("/after/{boardId}")
    private ResponseEntity<?> afterBoardDetail(@PathVariable("boardId") Long boardId) {
        BoardDetailAfterRpDTO responseDTO = boardService.boardDetailAfter(boardId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
}
