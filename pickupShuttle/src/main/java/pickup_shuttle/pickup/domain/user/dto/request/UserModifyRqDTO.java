package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserModifyRqDTO(
        @NotBlank(message = "은행명이 공백입니다.")
        String bank,
        @NotBlank(message = "계좌번호가 공백입니다.")
        String account
)
{ }
