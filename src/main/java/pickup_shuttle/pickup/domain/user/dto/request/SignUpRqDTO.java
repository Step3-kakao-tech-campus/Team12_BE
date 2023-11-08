package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;

@Builder
public record SignUpRqDTO (
        @NotBlank(message = "은행명" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "은행명" + ErrorMessage.BADREQUEST_SIZE)
        String bankName,
        @NotBlank(message = "계좌번호" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "계좌번호" + ErrorMessage.BADREQUEST_SIZE)
        String accountNum
) {}
