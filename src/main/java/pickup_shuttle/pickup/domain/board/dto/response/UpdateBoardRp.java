package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record UpdateBoardRp(
        Long boardId,
        String store,
        List<String> beverage,
        String destination,
        int tip,
        String request,
        Long finishedAt,
        boolean match
) {
}