package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record SignUpRqDTO (
    @NotEmpty(message = "은행 이름이 공백입니다.")
     String bankName,
    @NotEmpty(message = "계좌번호가 공백입니다.")
     String accountNum
) {}
