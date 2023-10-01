package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRqDTO
        (@NotBlank(message = "닉네임이 공백입니다")
         String nickname,
        @Size(min = 10, message = "계좌번호는 최소 10자리 이상입니다")
        @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력하세요")
         String account,
        @NotBlank(message = "은행명이 공백입니다")
         String bank
        ) { }
