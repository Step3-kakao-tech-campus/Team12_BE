package pickup_shuttle.pickup.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import pickup_shuttle.pickup.domain.account.Account;
import pickup_shuttle.pickup.domain.user.User;

public class UserRequest {

    @Getter @Setter
    public static class RegisterDTO{
        @NotBlank(message = "닉네임이 공백입니다")
        private String nickname;
        @Size(min = 10, message = "계좌번호는 최소 10자리 이상입니다")
        @Pattern(regexp = "^[0-9]+$", message = "숫자만 입력하세요")
        private String account;
        @NotBlank(message = "은행명이 공백입니다")
        private String bank;


        public Account toAccount(User user){
            return Account.builder()
                    .user(user)
                    .number(account)
                    .bank(bank)
                    .build();
        }
    }

    @Getter @Setter
    public static class NicknameCheckDTO{
        @NotBlank(message = "닉네임 값이 공백입니다")
        private String nickname;
    }

}
