package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.ValidValue;

@Builder
public record ApproveUserRq(
        @NotNull(message = "유저ID" + ErrorMessage.BADREQUEST_EMPTY)
        @Min(value = ValidValue.LONG_MIN, message = "유저ID는 1 이상이어야 합니다")
        Long userId
) {
}
