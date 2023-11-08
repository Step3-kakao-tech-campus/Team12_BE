package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.BeverageDTO;

import java.util.List;

@Builder
public record BoardModifyRpDTO(
        Long boardId,
        String store,
        List<BeverageDTO> beverage,
        String destination,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch
) {
}