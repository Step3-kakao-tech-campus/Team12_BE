package pickup_shuttle.pickup.domain.utils;

import org.springframework.stereotype.Component;
import pickup_shuttle.pickup.domain.board.Board;

import java.time.LocalDateTime;

@Component
public class Utils {
    public boolean overDeadline(Board board) {
        return board.getFinishedAt().isBefore(LocalDateTime.now());
    }
}
