package pickup_shuttle.pickup.domain.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pickup_shuttle.pickup.domain.board.Board;

import java.time.LocalDateTime;

@Component
@Slf4j
public class Utils {
    public static boolean notOverDeadline(Board board) {
        log.info("board.getFinishedAt() = {}, boardId = {}", board.getFinishedAt(),board.getBoardId());
        log.info("LocalDateTime.now() = {}, boardId = {}", LocalDateTime.now(), board.getBoardId());
        return board.getFinishedAt().isAfter(LocalDateTime.now());
    }
    //static 멤버들로 구성된 클래스를 정의 할 떄 non-public한 생성자 명시
    private Utils() {
    }
}
