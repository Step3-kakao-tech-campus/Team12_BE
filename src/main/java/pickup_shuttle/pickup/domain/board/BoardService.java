package pickup_shuttle.pickup.domain.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception403;
import pickup_shuttle.pickup._core.errors.exception.Exception404;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.beverage.dto.request.BeverageRq;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRp;
import pickup_shuttle.pickup.domain.board.dto.request.AcceptBoardRq;
import pickup_shuttle.pickup.domain.board.dto.request.UpdateBoardRq;
import pickup_shuttle.pickup.domain.board.dto.request.CreateBoardRq;
import pickup_shuttle.pickup.domain.board.dto.response.*;
import pickup_shuttle.pickup.domain.board.repository.BoardRepository;
import pickup_shuttle.pickup.domain.board.repository.BoardRepositoryCustom;
import pickup_shuttle.pickup.domain.match.Match;
import pickup_shuttle.pickup.domain.match.MatchRepository;
import pickup_shuttle.pickup.domain.match.MatchService;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.store.StoreRepository;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.repository.UserRepository;
import pickup_shuttle.pickup.utils.Utils;


import java.lang.reflect.Field;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepositoryCustom boardRepositoryCustom;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final MatchService matchService;
    private final MatchRepository matchRepository;

    public Slice<ReadBoardListRp> boardList(Long lastBoardId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        Slice<Board> boardsSlice = boardRepositoryCustom.searchAllBySlice(lastBoardId, pageRequest);
        return getBoardListResponseDTOs(pageRequest, boardsSlice);
    }

    //boardSlice로 ReadBoardListRp 만드는 과정
    private Slice<ReadBoardListRp> getBoardListResponseDTOs(PageRequest pageRequest, Slice<Board> boardSlice) {
        List<ReadBoardListRp> boardReadBoardListRp = boardSlice.getContent().stream()
                .map(b -> ReadBoardListRp.builder()
                        .boardId(b.getBoardId())
                        .shopName(b.getStore().getName())
                        .finishedAt(b.getFinishedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
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
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "유저ID", "유저"))
        );
        Store store = storeRepository.findByName(requestDTO.shopName()).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "가게명", "가게"))
        );
        Board board = requestDTO.toBoard(user, store);
        boardRepository.save(board);

        return CreateBoardRp.builder()
                .boardId(board.getBoardId())
                .build();
    }
    private ReadBoardBeforeRp boardDetailBefore(Long boardId, Long userId) {
        Board board = boardRepository.mfindByBoardId(boardId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글"))
        );
        List<BeverageRp> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRp.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return ReadBoardBeforeRp.builder()
                .boardId(board.getBoardId())
                .destination(board.getDestination())
                .request(board.getRequest())
                .tip(board.getTip())
                .finishedAt(board.getFinishedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
                .isMatch(board.isMatch())
                .shopName(board.getStore().getName())
                .beverages(beverageRpDTOS)
                .isRequester(board.getUser().getUserId().equals(userId))
                .build();
    }
    //select 2번
    private ReadBoardAfterRp boardDetailAfter(Long boardId, Long userId) {
        Board board = boardRepository.m2findByBoardId(boardId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글"))
        );
        User user = userRepository.findById(board.getMatch().getUser().getUserId()).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "매칭된 공고글의 유저ID", "유저"))
        );
        List<BeverageRp> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRp.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return ReadBoardAfterRp.builder()
                .boardId(board.getBoardId())
                .destination(board.getDestination())
                .request(board.getRequest())
                .tip(board.getTip())
                .finishedAt(board.getFinishedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
                .isMatch(board.isMatch())
                .shopName(board.getStore().getName())
                .pickerBank(user.getBank())
                .pickerAccount(user.getAccount())
                .pickerPhoneNumber(user.getPhoneNumber())
                .arrivalTime(board.getMatch().getMatchTime().plusMinutes(board.getMatch().getArrivalTime()).atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
                .isMatch(board.isMatch())
                .beverages(beverageRpDTOS)
                .isRequester(board.getUser().getUserId().equals(userId))
                .beverages(beverageRpDTOS)
                .build();
    }
    public CheckBeforeAfter checkBeforeAfter(Long boardId, Long userId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글"))
        );
        if(board.isMatch()) {
            return boardDetailAfter(boardId, userId);
        }
        return boardDetailBefore(boardId, userId);
    }

    @Transactional
    public AcceptBoardRp boardAgree(AcceptBoardRq requestDTO, Long boardId, Long userId) {
        Board board = boardRepository.mfindByBoardId(boardId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글"))
        );
        if(board.getMatch() != null) {
            throw new Exception403("공고글이 이미 매칭된 경우 공고글을 수락할 수 없습니다");
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "유저ID", "유저"))
        );
        Match match = matchService.createMatch(requestDTO.arrivalTime(),user);
        if(Objects.equals(match.getUser().getUserId(), board.getUser().getUserId())) {
            throw new Exception403("공고글 작성자가 매칭 수락을 시도하는 경우 공고글을 수락 할 수 없습니다");
        }
        board.updateMatch(match);
        board.updateIsMatch(true);
        List<BeverageRp> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRp.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return AcceptBoardRp.builder()
                .beverages(beverageRpDTOS)
                .shopName(board.getStore().getName())
                .tip(board.getTip())
                .destination(board.getDestination())
                .finishedAt(board.getFinishedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
                .request(board.getRequest())
                .arrivalTime(board.getMatch().getMatchTime().plusMinutes(board.getMatch().getArrivalTime()).atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
                .build();
    }

    @Transactional
    public DeleteBoardRp boardDelete(Long boardId, Long userId){
        // 공고글 확인
        Board board = boardRepository.m3findByBoardId(boardId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글"))
        );
        // 공고글 작성자 확인
        if(!(board.getUser().getUserId().equals(userId)))
            throw new Exception403("공고글의 작성자가 아닌 경우 공고글을 삭제할 수 없습니다");
        // 매칭되었는지 확인
        if(board.isMatch())
            throw new Exception403("공고글이 이미 매칭된 경우 공고글을 삭제할 수 없습니다");
        // 삭제
        boardRepository.delete(board);
        return DeleteBoardRp.builder()
                .message("공고글 삭제를 완료하였습니다")
                .build();
    }
    @Transactional
    public UpdateBoardRp update(UpdateBoardRq requestDTO, Long boardId, Long userId){
        // 공고글 확인
        Board board = boardRepository.m4findByBoardId(boardId).orElseThrow(
                () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "공고글ID", "공고글"))
        );
        // 공고글 작성자 확인
        if(!(board.getUser().getUserId().equals(userId)))
            throw new Exception403("공고글의 작성자가 아닌 경우 공고글을 수정할 수 없습니다");
        // 매칭 여부 확인
        if(board.isMatch())
            throw new Exception403("공고글이 이미 매칭된 경우 공고글을 수정할 수 없습니다");
        // 가게 확인
        Store store = null;
        if(requestDTO.shopName() != null){
            store = storeRepository.findByName(requestDTO.shopName()).orElseThrow(
                    () -> new Exception404(String.format(ErrorMessage.NOTFOUND_FORMAT, "가게명", "가게"))
            );
        }
        // 공고 수정
        Map<String, Object> mapToPatch = requestDTO.patchValues(store); // null 삭제
        if (!mapToPatch.isEmpty()){
            updatePatch(board, mapToPatch);
        } else{
            throw new Exception403("수정할 값이 없는 경우 공고글을 수정할 수 없습니다");
        }

        List<BeverageRp> beverageRpDTOS = board.getBeverages().stream().map(
                b -> BeverageRp.builder()
                        .name(b.getName())
                        .build()
        ).toList();

        return UpdateBoardRp.builder()
                .boardId(board.getBoardId())
                .shopName(board.getStore().getName())
                .beverages(beverageRpDTOS)
                .destination(board.getDestination())
                .tip(board.getTip())
                .request(board.getRequest())
                .finishedAt(board.getFinishedAt().atZone(ZoneId.of("Asia/Seoul")).toEpochSecond())
                .isMatch(board.isMatch())
                .build();
    }
    public void updatePatch(Board board, Map<String, Object> mapToPatch){
        mapToPatch.forEach((k, v) -> {
            if (k.equals("beverages")){
                board.getBeverages().clear();
                board.getBeverages().addAll((List<Beverage>)v);
                board.getBeverages().forEach(b -> b.setBoard(board));
            }
            else{
                Field field = ReflectionUtils.findField(Board.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, board, v);
            }
        });
    }

    public void checkListEmpty(List<BeverageRq> beverages){ //[](빈배열)만 보내는 경우 검사
        if(beverages != null && beverages.size() == 0){
            throw new Exception400("음료" + ErrorMessage.BADREQUEST_EMPTY);
        }
    }
}