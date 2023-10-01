package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception500;
import pickup_shuttle.pickup.domain.beverage.BeverageRepository;
import pickup_shuttle.pickup.domain.board.dto.request.WriteRqDTO;
import pickup_shuttle.pickup.domain.board.dto.response.BoardListRpDTO;
import pickup_shuttle.pickup.domain.board.dto.response.WriteRpDTO;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.store.StoreRepository;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRepository;


import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final BeverageRepository beverageRepository;

    public Slice<BoardListRpDTO> boardList(Long lastBoardId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "boardId"));
        Slice<Board> boardsSlice = boardRepositoryCustom.searchAllBySlice(lastBoardId, pageRequest);
        return getBoardListResponseDTOs(pageRequest, boardsSlice);
    }

    private Slice<BoardListRpDTO> getBoardListResponseDTOs(PageRequest pageRequest, Slice<Board> boardSlice) {
        List<BoardListRpDTO> boardBoardListRpDTO = boardSlice.getContent().stream()
                .map(b -> BoardListRpDTO.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .finishedAt(b.getFinishAt().toEpochSecond(ZoneOffset.UTC))
                        .tip(b.getTip())
                        .match(b.isMatch())
                        .build())
                .collect(Collectors.toList());
        return new SliceImpl<>(boardBoardListRpDTO,pageRequest,boardSlice.hasNext());
    }

    @Transactional
    public WriteRpDTO write(WriteRqDTO requestDTO, CustomOauth2User customOauth2User){

        User user = userRepository.findBySocialId(customOauth2User.getName()).orElseThrow(
                () -> new Exception400("유저가 존재하지 않습니다")
        );
        Store store = storeRepository.findByName(requestDTO.getStore()).orElseThrow(
                () -> new Exception400("가게가 존재하지 않습니다")
        );
        Board board = requestDTO.toBoard(user, store);
        try {
            boardRepository.save(board);
            beverageRepository.save(requestDTO.toBeverage(board));
        }
        catch (Exception e) {
            throw new Exception500("unknown server error");
        }

        return new WriteRpDTO(board.getBoardId());
    }
}