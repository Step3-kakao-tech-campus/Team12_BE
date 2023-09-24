package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain.errors.exception.Exception400;
import pickup_shuttle.pickup.domain.user.security.JwtTokenUtil;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserJPARepository userJPARepository;



    public String login(UserRequest.LoginDTO requestDTO){
        User user = userJPARepository.findUserById(requestDTO.getUid()).orElseThrow(
                () -> new Exception400("아이디가 잘못되었습니다. : " + requestDTO.getUid()));

        if(!passwordEncoder.matches(requestDTO.getPassword(), user.getPwd())) {
            throw new Exception400("패스워드가 잘못되었습니다.");
        }
        return JwtTokenUtil.createToken(user.getUid(),JwtTokenUtil.SECRET,JwtTokenUtil.EXP);
    }

}
