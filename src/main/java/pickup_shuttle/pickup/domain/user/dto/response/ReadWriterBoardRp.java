package pickup_shuttle.pickup.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.response.BeverageRp;

import java.util.List;

@Builder
public record ReadWriterBoardRp(
        Long boardId,
        String shopName,
        String destination,
        List<BeverageRp> beverages,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String pickerBank,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String pickerAccount,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Long arrivalTime,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String pickerPhoneNumber
) { }
