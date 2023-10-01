package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
<<<<<<< HEAD
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
=======
import lombok.Builder;

@Builder
public record SignUpRqDTO (
    @NotEmpty(message = "은행 이름이 공백입니다.")
     String bankName,
    @NotEmpty(message = "계좌번호가 공백입니다.")
     String accountNum
) {}
>>>>>>> 7bf9a69863910e0a198ac312c3cbac9913aa5fc8
