package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain._core.errors.exception.Exception400;
import pickup_shuttle.pickup.domain._core.errors.exception.Exception500;
import pickup_shuttle.pickup.domain.account.AccountRepository;
import pickup_shuttle.pickup.domain.user.security.JwtTokenUtil;

import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    // 로그인 후 JWT 토큰 반환
    public String login(UserRequest.LoginDTO requestDTO){
        User user = userRepository.findUserById(requestDTO.getUid()).orElseThrow(
                () -> new Exception400("아이디가 잘못되었습니다. : " + requestDTO.getUid()));

        if(!passwordEncoder.matches(requestDTO.getPassword(), user.getPwd())) {
            throw new Exception400("패스워드가 잘못되었습니다.");
        }
        return JwtTokenUtil.createToken(user.getUid(),JwtTokenUtil.SECRET,JwtTokenUtil.EXP);
    }

    @Transactional
    public void register(UserRequest.RegisterDTO requestDTO){
        checkNicknameDuplicate(requestDTO.getNickname());
        Long userId = 1L; // 로그인 구현 후 변경
        User userPS = userRepository.findById(userId).orElseThrow(
                () -> new Exception400("유저가 존재하지 않습니다")
        );
        try{
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
