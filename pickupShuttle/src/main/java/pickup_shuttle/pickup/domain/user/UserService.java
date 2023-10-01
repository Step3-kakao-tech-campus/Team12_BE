package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<<<<<<< HEAD
import pickup_shuttle.pickup.domain.account.Account;
import pickup_shuttle.pickup.domain.account.AccountRepository;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
=======
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception500;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.RegisterRqDTO;
>>>>>>> 7bf9a69863910e0a198ac312c3cbac9913aa5fc8
import pickup_shuttle.pickup.domain.user.dto.request.SignUpRqDTO;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void signup(SignUpRqDTO signUpRqDTO, CustomOauth2User customOauth2User){
        String bankName = signUpRqDTO.bankName();
        String accountNum = signUpRqDTO.accountNum();
        Optional<User> user = userRepository.findBySocialId(customOauth2User.getName());
        user.get().setRole(UserRole.USER);
        customOauth2User.setBankName(bankName);
        customOauth2User.setAccountNum(accountNum);
        user.get().setBank(bankName);
        user.get().setAccount(accountNum);
        return;
    }

}
