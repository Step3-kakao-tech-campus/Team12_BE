package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.BeverageDTO;

import java.util.List;

@Builder
public record BoardAgreeRpDTO(
        String shopName,
        String destination,
        List<BeverageDTO> beverage,
        int tip,
        String request,
        Long finishedAt,
        Long arrivalTime
) { }
