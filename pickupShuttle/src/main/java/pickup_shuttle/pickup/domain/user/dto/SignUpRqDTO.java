package pickup_shuttle.pickup.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRqDTO {
    @NotEmpty(message = "은행 이름이 공백입니다.")
    private String bankName;

    @NotEmpty(message = "계좌번호가 공백입니다.")
    private String accountNum;
}
