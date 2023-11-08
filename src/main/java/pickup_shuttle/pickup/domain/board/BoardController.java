package pickup_shuttle.pickup.domain.board;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pickup_shuttle.pickup._core.utils.ApiUtils;
import pickup_shuttle.pickup._core.utils.CustomPage;
import pickup_shuttle.pickup.config.Login;
import pickup_shuttle.pickup.domain.board.dto.request.BoardAgreeRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardModifyRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardWriteRqDTO;
import pickup_shuttle.pickup.domain.board.dto.response.*;
import pickup_shuttle.pickup.security.service.JwtService;


@RestController
@RequiredArgsConstructor
@RequestMapping("articles")
public class BoardController {
    private final BoardService boardService;
    private final JwtService jwtService;
    @GetMapping
    public ResponseEntity<ApiUtils.ApiResult<CustomPage<BoardListRpDTO>>> getBoardList(
            @RequestParam(value = "offset",required = false) Long lastBoardId,
            @RequestParam(value = "limit",defaultValue = "10") int size) {
        Slice<BoardListRpDTO> responseDTOSlice = boardService.boardList(lastBoardId, size);
        return ResponseEntity.ok(ApiUtils.success(new CustomPage<>(responseDTOSlice)));
    }

    @PostMapping("/write")
    public ResponseEntity<ApiUtils.ApiResult<BoardWriteRpDTO>> write(@RequestBody @Valid BoardWriteRqDTO requestDTO,
                                                      @Login Long userId){
        boardService.checkListBlank(requestDTO.beverage());
        BoardWriteRpDTO responseDTO = boardService.write(requestDTO, userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiUtils.ApiResult<CheckBeforeAfter>> boardDetail(@PathVariable("boardId") Long boardId) {
        CheckBeforeAfter responseDTO = boardService.checkBeforeAfter(boardId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @PostMapping("/agree/{boardId}")
    public ResponseEntity<ApiUtils.ApiResult<BoardAgreeRpDTO>> pickupAgree(@PathVariable("boardId") Long boardId,
                                         @RequestBody @Valid BoardAgreeRqDTO requestDTO,
                                         @Login Long userId) {
        BoardAgreeRpDTO responseDTO = boardService.boardAgree(requestDTO,boardId,userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<ApiUtils.ApiResult<String>> delete(@PathVariable("boardId") Long boardId,
                                                     @Login Long userId){
        boardService.boardDelete(boardId, userId);
        return ResponseEntity.ok(ApiUtils.success("공고글 삭제 완료"));
    }
    @PatchMapping("/modify/{boardId}")
    public ResponseEntity<ApiUtils.ApiResult<BoardModifyRpDTO>> modify(@PathVariable("boardId") Long boardId,
                                                     @RequestBody @Valid BoardModifyRqDTO requestDTO,
                                                     @Login Long userId){
        boardService.checkListEmpty(requestDTO.beverage());
        BoardModifyRpDTO responseDTO = boardService.modify(requestDTO,boardId, userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
}