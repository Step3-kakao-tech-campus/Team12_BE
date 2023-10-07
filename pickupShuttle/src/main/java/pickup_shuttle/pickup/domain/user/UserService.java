package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
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
    public String userAuthStatus(long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("유저를 찾을 수 없습니다")
        );
        String userRole = user.getUserRole().getValue();
        String userUrl = user.getUrl();
        switch (userRole){
            case "ROLE_USER": return userUrl.equals("") ? "미인증":"인증 진행 중";
            case "ROLE_STUDENT": return "인증";
            default : return "미인증";
        }
    }

}
