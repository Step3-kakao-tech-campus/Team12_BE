package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record NicknameCheckRqDTO(
        @NotBlank(message = "닉네임 값이 공백입니다")
         String nickname
) { }
