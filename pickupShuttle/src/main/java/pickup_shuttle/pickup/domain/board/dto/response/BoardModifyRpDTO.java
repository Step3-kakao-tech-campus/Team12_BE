package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.board.dto.request.BoardModifyRqDTO;

import java.time.ZoneOffset;
import java.util.List;

@Builder
public record BoardModifyRpDTO(
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