package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.Beverage;

import java.util.List;

@Builder
public record ReadBoardAfterRp(
        Long boardId,
        String shopName,
        String destination,
        List<Beverage> beverage,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch,
        String pickerBank,
        String pickerAccount,
        Long arrivalTime,
        String pickerPhoneNumber
) implements CheckBeforeAfter{ }
