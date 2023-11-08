package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;

@Builder
public record UserAuthApproveRqDTO(
        @NotNull(message = "유저ID" + ErrorMessage.BADREQUEST_BLANK)
        @Min(value = 1, message = "유저ID" + ErrorMessage.BADREQUEST_MIN)
        @Max(value = Integer.MAX_VALUE, message = "유저ID" + ErrorMessage.BADREQUEST_MAX)
        Long userId
) {
}
