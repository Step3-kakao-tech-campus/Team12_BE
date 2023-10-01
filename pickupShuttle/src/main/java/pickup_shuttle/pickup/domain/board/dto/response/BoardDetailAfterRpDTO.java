package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;

@Builder
public record BoardDetailAfterRpDTO (
        Long boardId,
        String shopName,
        String destination,
        String beverage,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch,
        String pickerBank,
        String pickerAccount,
        Long arrivalTime,
        String pickerPhoneNumber
) { }
