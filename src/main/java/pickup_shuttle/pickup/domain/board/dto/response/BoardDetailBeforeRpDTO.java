package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRpDTO;

import java.util.List;

@Builder
public record BoardDetailBeforeRpDTO(
        Long boardId,
        String shopName,
        String destination,
        List<BeverageRpDTO> beverages,
        Integer tip,
        String request,
        Long finishedAt,
        boolean isMatch,
        boolean isRequester
) { }
