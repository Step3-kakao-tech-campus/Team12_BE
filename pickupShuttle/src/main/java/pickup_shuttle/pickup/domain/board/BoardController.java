package pickup_shuttle.pickup.domain.board;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.utils.ApiUtils;

import pickup_shuttle.pickup.domain.board.dto.request.WriteRqDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardListRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.WriteRpDTO;

import pickup_shuttle.pickup.domain.board.dto.response.BoardDetailAfterRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardDetailBeforeRpDTO;
import pickup_shuttle.pickup.security.service.JwtService;


@RestController
@RequiredArgsConstructor
@RequestMapping("articles")
public class BoardController {
    private final BoardService boardService;
    private final JwtService jwtService;
    @GetMapping
    public ResponseEntity<?> getBoardList(
            @RequestParam(value = "offset" +
                    "", required = false) Long lastBoardId,
            @RequestParam(value = "limit",defaultValue = "10") int size) {
        Slice<BoardListRpDTO> responseDTOSlice = boardService.boardList(lastBoardId, size);
        return ResponseEntity.ok(ApiUtils.success(responseDTOSlice));
    }

    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestBody @Valid WriteRqDTO requestDTO, HttpServletRequest request){
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(
                () -> new Exception400("access token을 추출하지 못했습니다")
        );
        String userId = jwtService.extractEmail(accessToken).orElseThrow(
                () -> new Exception400("user의 Id를 추출하지 못했습니다")
        );
        WriteRpDTO responseDTO = boardService.write(requestDTO, userId);
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
