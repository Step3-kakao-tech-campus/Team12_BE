package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup._core.errors.exception.Exception500;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.RegisterRqDTO;
import pickup_shuttle.pickup.domain.user.dto.request.SignUpRqDTO;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    // private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void register(RegisterRqDTO requestDTO){
        checkNicknameDuplicate(requestDTO.nickname());
        Long userId = 1L; // 로그인 구현 후 변경
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("유저가 존재하지 않습니다")
        );
        try{
            userPS.updateNickname(requestDTO.nickname());
         //   accountRepository.save(requestDTO.toAccount(userPS));

        } catch (Exception e) {
            throw new Exception500("unknown server error");
        }
    }

    public void checkNicknameDuplicate(String nickname){
        Optional<User> userOP = userRepository.findByNickname(nickname);
        if (userOP.isPresent()){
            throw new Exception400("동일한 닉네임이 존재합니다");
        }
    }

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
