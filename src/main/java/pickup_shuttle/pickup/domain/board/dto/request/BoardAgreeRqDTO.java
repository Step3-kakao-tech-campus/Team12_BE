package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BoardAgreeRqDTO(
        @NotNull(message = "도착예정 시간이 공백입니다")
        @Min(value = 0, message = "도착예정 시간은 0 이상이어야 합니다")
        @Max(value = 60, message = "도착예정 시간은 60 이하이어야 합니다")
        Integer arrivalTime
) { }
