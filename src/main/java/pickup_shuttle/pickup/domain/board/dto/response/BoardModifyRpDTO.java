package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRpDTO;

import java.util.List;

@Builder
public record BoardModifyRpDTO(
        Long boardId,
        String shopName,
        List<BeverageRpDTO> beverages,
        String destination,
        Integer tip,
        String request,
        Long finishedAt,
        boolean isMatch
) { }