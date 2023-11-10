package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRp;
import java.util.List;

@Builder
public record AcceptBoardRp(
        String shopName,
        String destination,
        List<BeverageRp> beverages,
        int tip,
        String request,
        Long finishedAt,
        Long arrivalTime
) { }
