package pickup_shuttle.pickup.domain.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

public class UserRequest {

    @Getter
    @Setter
    public static class LoginDTO {
        @NotEmpty
        private String uid;

        @NotEmpty
        private String password;
    }

}
