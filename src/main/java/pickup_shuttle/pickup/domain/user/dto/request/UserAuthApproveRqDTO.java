package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.ValidValue;

@Builder
public record UserAuthApproveRqDTO(
        @NotNull(message = "유저ID" + ErrorMessage.BADREQUEST_EMPTY)
        @Min(value = ValidValue.LONG_MIN, message = "유저ID는 1 이상이어야 합니다")
        @Max(value = ValidValue.LONG_MAX, message = "유저ID는 Long의 최대값 이하이어야 합니다")
        Long userId
) {
}
