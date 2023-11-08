package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;

@Builder
public record BoardAgreeRqDTO(
        @NotNull(message = "도착예정시간" + ErrorMessage.BADREQUEST_BLANK)
        @Min(value = 1, message = "도착예정시간" + ErrorMessage.BADREQUEST_MIN)
        @Max(value = Integer.MAX_VALUE, message = "도착예정시간" + ErrorMessage.BADREQUEST_MAX)
        Integer arrivalTime
) { }
