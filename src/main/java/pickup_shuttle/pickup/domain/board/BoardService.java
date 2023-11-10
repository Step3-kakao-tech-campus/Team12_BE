package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception500;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.beverage.dto.Beverage;
import pickup_shuttle.pickup.domain.board.dto.request.AcceptBoardRq;
import pickup_shuttle.pickup.domain.board.dto.request.UpdateBoardRq;
import pickup_shuttle.pickup.domain.board.dto.request.CreateBoardRq;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MatchService matchService;

    public Slice<ReadBoardListRp> boardList(Long lastBoardId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        Slice<Board> boardsSlice = boardRepositoryCustom.searchAllBySlice(lastBoardId, pageRequest);
        return getBoardListResponseDTOs(pageRequest, boardsSlice);
    }

    //boardSlice로 ReadBoardListRp 만드는 과정
    private Slice<ReadBoardListRp> getBoardListResponseDTOs(PageRequest pageRequest, Slice<Board> boardSlice) {
        List<ReadBoardListRp> boardReadBoardListRp = boardSlice.getContent().stream()
                .filter(Utils::notOverDeadline)
                .map(b -> ReadBoardListRp.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .finishedAt(b.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                        .tip(b.getTip())
                        .isMatch(b.isMatch())
                        .destination(b.getDestination())
                        .build())
                .toList();
        return new SliceImpl<>(boardReadBoardListRp,pageRequest,boardSlice.hasNext());
    }

    @Transactional
    public CreateBoardRp write(CreateBoardRq requestDTO, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        Store store = storeRepository.findByName(requestDTO.store()).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_STORE)
        );
        Board board = requestDTO.toBoard(user, store);
        try {
            boardRepository.save(board);
        } catch (Exception e) {
            throw new Exception500("unknown server error");
        }

        return new CreateBoardRp(board.getBoardId());
    }
    private ReadBoardBeforeRp boardDetailBefore(Long boardId) {
        Board board = boardRepository.mfindByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        List<Beverage> beverages = board.getBeverages().stream().map(
                b -> Beverage.builder()
                        .name(b.getName())
                        .build()
        ).toList();
        return ReadBoardBeforeRp.builder()
                .boardId(board.getBoardId())
                .destination(board.getDestination())
                .request(board.getRequest())
                .tip(board.getTip())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .isMatch(board.isMatch())
                .shopName(board.getStore().getName())
                .beverage(beverages)
                .build();
    }
    //select 2번
    private ReadBoardAfterRp boardDetailAfter(Long boardId) {
        Board board = boardRepository.m2findByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        User user = userRepository.findById(board.getMatch().getUser().getUserId()).orElseThrow(
                () -> new Exception400("매칭 된 picker를 찾을 수 없습니다")
        );
        List<Beverage> beverages = board.getBeverages().stream().map(
                b -> Beverage.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return ReadBoardAfterRp.builder()
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
                .beverage(beverages)
                .build();
    }
    public CheckBeforeAfter checkBeforeAfter(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        if(board.isMatch()) {
            return boardDetailAfter(boardId);
        }
        return boardDetailBefore(boardId);
    }

    @Transactional
    public AcceptBoardRp boardAgree(AcceptBoardRq requestDTo, Long boardId, Long userId) {
        Board board = boardRepository.mfindByBoardId(boardId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_BOARD)
        );
        if(board.getMatch() != null) {
            throw new Exception400("공고글이 이미 매칭 됐습니다");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        Match match = matchService.createMatch(requestDTo.arrivalTime(),user);
        if(Objects.equals(match.getUser().getUserId(), board.getUser().getUserId())) {
            throw new Exception400("공고글 작성자는 매칭 수락을 할 수 없습니다");
        }
        board.updateMatch(match);
        board.updateIsMatch(true);
        List<Beverage> beverages = board.getBeverages().stream().map(
                b -> Beverage.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return AcceptBoardRp.builder()
                .beverage(beverages)
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
        try {
            boardRepository.delete(board);
        } catch (Exception e){
            throw new Exception500("unknown server error");
        }
    }
    @Transactional
    public UpdateBoardRp modify(UpdateBoardRq requestDTO, Long boardId, Long userId){
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
        if (!mapToPatch.isEmpty()){
            updatePatch(board, mapToPatch);
        } else{
            throw new Exception400("수정할 값이 없습니다");
        }

        List<String> beverages = board.getBeverages().stream().map(pickup_shuttle.pickup.domain.beverage.Beverage::getName).toList();
        return UpdateBoardRp.builder()
                .boardId(board.getBoardId())
                .store(board.getStore().getName())
                .beverage(beverages)
                .destination(board.getDestination())
                .tip(board.getTip())
                .request(board.getRequest())
                .finishedAt(board.getFinishedAt().toEpochSecond(ZoneOffset.UTC))
                .match(board.isMatch())
                .build();
    }
    public void updatePatch(Board board, Map<String, Object> mapToPatch){
        mapToPatch.forEach((k, v) -> {
            if (k.equals("beverages")){
                board.getBeverages().clear();
                board.getBeverages().addAll((List<pickup_shuttle.pickup.domain.beverage.Beverage>)v);
                board.getBeverages().forEach(b -> b.setBoard(board));
            }
            else{
                Field field = ReflectionUtils.findField(Board.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, board, v);
            }

        });
    }

    public void checkListBlank(List<String> beverages) {
        for(String b : beverages) {
            if(b == null || b.isEmpty())
                throw new Exception400("음료명에 빈 문자열 or null이 입력 되었습니다");
        }
    }
    public void checkListEmpty(List<String> beverages){
        if(beverages != null && beverages.isEmpty()){
            throw new Exception400("음료를 입력하지 않았습니다");
        }
    }
}