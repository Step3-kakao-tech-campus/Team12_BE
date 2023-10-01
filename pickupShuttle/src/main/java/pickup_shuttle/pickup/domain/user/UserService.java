package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain.account.Account;
import pickup_shuttle.pickup.domain.account.AccountRepository;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.SignUpRqDTO;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;


    public void signup(SignUpRqDTO signUpRqDTO, CustomOauth2User customOauth2User){
        String bankName = signUpRqDTO.getBankName();
        String accountNum = signUpRqDTO.getAccountNum();
        Optional<User> user = userRepository.findBySocialId(customOauth2User.getName());
        user.get().setRole(UserRole.USER);
        customOauth2User.setBankName(bankName);
        customOauth2User.setAccountNum(accountNum);
        Account account = new Account(user.get(),accountNum,bankName);
        accountRepository.save(account);
        return;
    }

}
