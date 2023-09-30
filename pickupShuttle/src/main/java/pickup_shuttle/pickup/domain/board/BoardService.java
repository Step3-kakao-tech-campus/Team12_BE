package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain.board.dto.response.BoardListResponseDTO;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepositoryCustom boardRepositoryCustom;

    public Slice<BoardListResponseDTO> boardList(Long lastBoardId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "boardId"));
        Slice<Board> boardsSlice = boardRepositoryCustom.searchAllBySlice(lastBoardId, pageRequest);
        return getBoardListResponseDTOs(pageRequest, boardsSlice);
    }

    private Slice<BoardListResponseDTO> getBoardListResponseDTOs(PageRequest pageRequest, Slice<Board> boardSlice) {
        List<BoardListResponseDTO> boardBoardListResponseDTO = boardSlice.getContent().stream()
                .map(b -> BoardListResponseDTO.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .finishedAt(b.getFinishAt().toEpochSecond(ZoneOffset.UTC))
                        .tip(b.getTip())
                        .match(b.isMatch())
                        .build())
                .collect(Collectors.toList());
        return new SliceImpl<>(boardBoardListResponseDTO,pageRequest,boardSlice.hasNext());
    }
}