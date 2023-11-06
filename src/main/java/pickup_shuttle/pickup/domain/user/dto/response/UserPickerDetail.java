package pickup_shuttle.pickup.domain.user.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.BeverageDTO;

import java.util.List;

@Builder
public record UserPickerDetail(
        Long boardId,
        String shopName,
        String destination,
        List<BeverageDTO> beverage,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch,
        String pickerBank,
        String pickerAccount,
        Long arrivalTime,
        String pickerPhoneNumber
) { }
