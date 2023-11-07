package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserAuthApproveRqDTO(
        @NotNull(message = "유저를 특정할 수 없습니다")Long userId
) {
}
