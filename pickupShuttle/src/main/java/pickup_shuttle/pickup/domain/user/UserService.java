package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.UserModifyRqDTO;
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
    public String userAuthStatus(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new Exception400(ErrorMessage.UNKNOWN_USER)
        );
        String userRole = user.getUserRole().getValue();
        String userUrl = user.getUrl();
        switch (userRole){
            case "ROLE_USER": return userUrl.equals("") ? "미인증":"인증 진행 중";
            case "ROLE_STUDENT": return "인증";
            default : return "미인증";
        }
    }

    @Transactional
    public boolean modifyUser(UserModifyRqDTO userModifyRqDTO, Long userId){
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()){
            return false;
        }
        String userBankName = user.get().getBank();
        String userAccountNum = user.get().getAccount();
        if(userModifyRqDTO.account() != userAccountNum){
            user.get().setAccount(userModifyRqDTO.account());
        }
        if(userModifyRqDTO.bank() != userBankName){
            user.get().setBank(userModifyRqDTO.bank());
        }
        return true;
    }

    public long userPK(CustomOauth2User customOauth2User){
        Optional<User> user = userRepository.findBySocialId(customOauth2User.getName());
        long userPK = user.get().getUserId();
        return userPK;
    }


}
