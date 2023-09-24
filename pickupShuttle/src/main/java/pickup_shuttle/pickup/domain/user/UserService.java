package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain._core.errors.exception.Exception400;
import pickup_shuttle.pickup.domain._core.errors.exception.Exception500;
import pickup_shuttle.pickup.domain.account.AccountRepository;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void register(UserRequest.RegisterDTO requestDTO){
        checkNicknameDuplicate(requestDTO.getNickname());
        Long userId = 1L; // 로그인 구현 후 변경

        try{
            User userPS = userRepository.findById(userId).orElseThrow(
                    () -> new Exception400("유저가 존재하지 않습니다")
            );
            userPS.updateNickname(requestDTO.getNickname());
            accountRepository.save(requestDTO.toAccount(userPS));

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
}
