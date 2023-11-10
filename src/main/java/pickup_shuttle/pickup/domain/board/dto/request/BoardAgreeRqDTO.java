package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.ValidValue;

@Builder
public record BoardAgreeRqDTO(
        @Min(value = ValidValue.INTEGER_MIN, message = "도착예정시간" + ErrorMessage.BADREQUEST_MIN)
        int arrivalTime
) { }
