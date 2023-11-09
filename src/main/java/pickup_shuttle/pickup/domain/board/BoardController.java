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


@RestController
@RequiredArgsConstructor
@RequestMapping("articles")
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<?> getBoardList(
            @RequestParam(value = "offset",required = false) Long lastBoardId,
            @RequestParam(value = "limit",defaultValue = "10") int size) {
        Slice<BoardListRpDTO> responseDTOSlice = boardService.boardList(lastBoardId, size);
        return ResponseEntity.ok(ApiUtils.success(new CustomPage(responseDTOSlice)));
    }

    @PostMapping("/write")
    public ResponseEntity<?> write(@RequestBody @Valid BoardWriteRqDTO requestDTO,
                                   @Login Long userId){
        BoardWriteRpDTO responseDTO = boardService.write(requestDTO, userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @GetMapping("/before/{boardId}")
    public ResponseEntity<?> beforeBoardDetail(@PathVariable("boardId") Long boardId,
                                               @Login Long userId) {
        BoardDetailBeforeRpDTO responseDTO = boardService.boardDetailBefore(boardId, userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }

    @GetMapping("/after/{boardId}")
    public ResponseEntity<?> afterBoardDetail(@PathVariable("boardId") Long boardId,
                                              @Login Long userId) {
        BoardDetailAfterRpDTO responseDTO = boardService.boardDetailAfter(boardId, userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
    @PostMapping("/agree/{boardId}")
    public ResponseEntity<?> pickupAgree(@PathVariable("boardId") Long boardId,
                                         @RequestBody @Valid BoardAgreeRqDTO requestDTO,
                                         @Login Long userId) {
        BoardAgreeRpDTO responseDTO = boardService.boardAgree(requestDTO,boardId,userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity<?> delete(@PathVariable("boardId") Long boardId,
                                    @Login Long userId){
        boardService.boardDelete(boardId, userId);
        return ResponseEntity.ok(ApiUtils.success("공고글 삭제 완료"));
    }
    @PatchMapping("/modify/{boardId}")
    public ResponseEntity<?> modify(@PathVariable("boardId") Long boardId,
                                    @RequestBody @Valid BoardModifyRqDTO requestDTO,
                                    @Login Long userId){
        boardService.checkListEmpty(requestDTO.beverages());
        BoardModifyRpDTO responseDTO = boardService.modify(requestDTO,boardId, userId);
        return ResponseEntity.ok(ApiUtils.success(responseDTO));
    }
}