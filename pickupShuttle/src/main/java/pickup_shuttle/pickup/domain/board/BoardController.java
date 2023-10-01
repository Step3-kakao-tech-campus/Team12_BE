package pickup_shuttle.pickup.domain.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pickup_shuttle.pickup._core.utils.ApiUtils;

import pickup_shuttle.pickup.domain.board.dto.request.WriteRqDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardListRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.WriteRpDTO;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;

import pickup_shuttle.pickup.domain.board.dto.response.BoardDetailAfterRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardDetailBeforeRpDTO;


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

    @PostMapping("/articles/write")
    public ResponseEntity<?> write(@RequestBody @Valid WriteRqDTO requestDTO,
                                   @AuthenticationPrincipal CustomOauth2User customOauth2User){
        WriteRpDTO responseDTO = boardService.write(requestDTO, customOauth2User);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
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
