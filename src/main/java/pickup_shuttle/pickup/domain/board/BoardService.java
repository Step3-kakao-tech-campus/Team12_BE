package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.beverage.dto.request.BeverageRqDTO;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRpDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardAgreeRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardModifyRqDTO;
import pickup_shuttle.pickup.domain.board.dto.request.BoardWriteRqDTO;
import pickup_shuttle.pickup.domain.board.dto.response.*;
import pickup_shuttle.pickup.domain.board.repository.BoardRepository;
import pickup_shuttle.pickup.domain.board.repository.BoardRepositoryCustom;
import pickup_shuttle.pickup.domain.match.Match;
import pickup_shuttle.pickup.domain.match.MatchService;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.store.StoreRepository;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.repository.UserRepository;
import pickup_shuttle.pickup.domain.utils.Utils;


import java.lang.reflect.Field;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MatchService matchService;

    public Slice<BoardListRpDTO> boardList(Long lastBoardId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        Slice<Board> boardsSlice = boardRepositoryCustom.searchAllBySlice(lastBoardId, pageRequest);
        return getBoardListResponseDTOs(pageRequest, boardsSlice);
    }

    //boardSlice로 BoardListRpDTO 만드는 과정
    private Slice<BoardListRpDTO> getBoardListResponseDTOs(PageRequest pageRequest, Slice<Board> boardSlice) {
        List<BoardListRpDTO> boardBoardListRpDTO = boardSlice.getContent().stream()
                .filter(Utils::notOverDeadline)
                .map(b -> BoardListRpDTO.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .finishedAt(b.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                        .tip(b.getTip())
                        .isMatch(b.isMatch())
                        .destination(b.getDestination())
                        .build())
                .toList();
        return new SliceImpl<>(boardBoardListRpDTO,pageRequest,boardSlice.hasNext());
    }

    @Transactional
    public BoardWriteRpDTO write(BoardWriteRqDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        Store store = storeRepository.findByName(requestDTO.store()).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_STORE)
        );
        Board board = requestDTO.toBoard(user, store);
        boardRepository.save(board);

        return BoardWriteRpDTO.builder()
                .boardId(board.getBoardId())
                .build();
    }
    public BoardDetailBeforeRpDTO boardDetailBefore(Long boardId) {
        Board board = boardRepository.mfindByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        List<BeverageRpDTO> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRpDTO.builder()
                        .name(b.getName())
                        .build()
        ).toList();
        return BoardDetailBeforeRpDTO.builder()
                .boardId(board.getBoardId())
                .destination(board.getDestination())
                .request(board.getRequest())
                .tip(board.getTip())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .isMatch(board.isMatch())
                .shopName(board.getStore().getName())
                .beverage(beverageRpDTOS)
                .build();
    }
    //select 2번
    public BoardDetailAfterRpDTO boardDetailAfter(Long boardId) {
        Board board = boardRepository.m2findByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        User user = userRepository.findById(board.getMatch().getUser().getUserId()).orElseThrow(
                () -> new Exception400("매칭 된 picker를 찾을 수 없습니다")
        );
        List<BeverageRpDTO> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRpDTO.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return BoardDetailAfterRpDTO.builder()
                .boardId(board.getBoardId())
                .destination(board.getDestination())
                .request(board.getRequest())
                .tip(board.getTip())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .isMatch(board.isMatch())
                .shopName(board.getStore().getName())
                .pickerBank(user.getBank())
                .pickerAccount(user.getAccount())
                .pickerPhoneNumber(user.getPhoneNumber())
                .arrivalTime(board.getMatch().getMatchTime().plusMinutes(board.getMatch().getArrivalTime()).toEpochSecond(ZoneOffset.UTC))
                .isMatch(board.isMatch())
                .beverage(beverageRpDTOS)
                .build();
    }

    @Transactional
    public BoardAgreeRpDTO boardAgree(BoardAgreeRqDTO requestDTO, Long boardId, Long userId) {
        Board board = boardRepository.mfindByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        if(board.getMatch() != null) {
            throw new Exception400("공고글이 이미 매칭 됐습니다");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        Match match = matchService.createMatch(requestDTO.arrivalTime(),user);
        if(match.getUser().getUserId() == board.getUser().getUserId()) {
            throw new Exception400("공고글 작성자는 매칭 수락을 할 수 없습니다");
        }
        board.updateMatch(match);
        board.updateIsMatch(true);
        List<BeverageRpDTO> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRpDTO.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return BoardAgreeRpDTO.builder()
                .beverage(beverageRpDTOS)
                .shopName(board.getStore().getName())
                .tip(board.getTip())
                .destination(board.getDestination())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .request(board.getRequest())
                .arrivalTime(board.getMatch().getMatchTime().plusMinutes(board.getMatch().getArrivalTime()).toEpochSecond(ZoneOffset.UTC))
                .build();
    }

    @Transactional
    public void boardDelete(Long boardId, Long userId){
        // 공고글 확인
        Board board = boardRepository.m3findByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        // 공고글 작성자 확인
        if(!(board.getUser().getUserId().equals(userId)))
            throw new Exception400("공고글의 작성자가 아닙니다");
        // 매칭되었는지 확인
        if(board.isMatch())
            throw new Exception400("이미 매칭된 공고글은 삭제 할 수 없습니다");
        // 삭제
        boardRepository.delete(board);

    }
    @Transactional
    public BoardModifyRpDTO modify(BoardModifyRqDTO requestDTO, Long boardId, Long userId){
        // 공고글 확인
        Board board = boardRepository.m4findByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        // 공고글 작성자 확인
        if(!(board.getUser().getUserId().equals(userId)))
            throw new Exception400("공고글의 작성자가 아닙니다");
        // 매칭 여부 확인
        if(board.isMatch())
            throw new Exception400("이미 매칭된 공고글은 수정 할 수 없습니다");
        // 가게 확인
        Store store = null;
        if(requestDTO.store() != null){
            store = storeRepository.findByName(requestDTO.store()).orElseThrow(
                    () -> new Exception400(ErrorMessage.UNKNOWN_STORE));
        }
        // 공고 수정
        Map<String, Object> mapToPatch = requestDTO.patchValues(store); // null 삭제
        if (mapToPatch.size() > 0){
            updatePatch(board, mapToPatch);
        } else{
            throw new Exception400("수정할 값이 없습니다");
        }

        List<BeverageRpDTO> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRpDTO.builder()
                        .name(b.getName())
                        .build()
        ).toList();
        return BoardModifyRpDTO.builder()
                .boardId(board.getBoardId())
                .store(board.getStore().getName())
                .beverage(beverageRpDTOS)
                .destination(board.getDestination())
                .tip(board.getTip())
                .request(board.getRequest())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .isMatch(board.isMatch())
                .build();
    }
    public void updatePatch(Board board, Map<String, Object> mapToPatch){
        mapToPatch.forEach((k, v) -> {
            if (k.equals("beverages")){
                board.getBeverages().clear();
                board.getBeverages().addAll((List<Beverage>)v);
                board.getBeverages().forEach(b -> {b.setBoard(board);});
            }
            else{
                Field field = ReflectionUtils.findField(Board.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, board, v);
            }

        });
    }

    public void checkListEmpty(List<BeverageRqDTO> beverages){ //[](빈배열)만 보내는 경우 검사
        if(beverages != null && beverages.size() == 0){
            throw new Exception400("음료" + ErrorMessage.BADREQUEST_EMPTY);
        }
    }
}