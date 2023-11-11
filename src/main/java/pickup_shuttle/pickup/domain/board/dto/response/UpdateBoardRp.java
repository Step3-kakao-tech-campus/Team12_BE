package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRp;

import java.util.List;

@Builder
public record UpdateBoardRp(
        Long boardId,
        String shopName,
        List<BeverageRp> beverages,
        String destination,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch
) { }