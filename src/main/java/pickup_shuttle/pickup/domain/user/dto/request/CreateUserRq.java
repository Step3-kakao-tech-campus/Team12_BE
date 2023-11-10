package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.ValidValue;

@Builder
public record CreateUserRq (
        @NotBlank(message = "은행명" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "은행명" + ErrorMessage.BADREQUEST_SIZE)
        String bankName,
        @NotBlank(message = "계좌번호" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "계좌번호" + ErrorMessage.BADREQUEST_SIZE)
        String accountNum
) {}
