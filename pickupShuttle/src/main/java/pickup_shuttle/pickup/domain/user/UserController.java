package pickup_shuttle.pickup.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pickup_shuttle.pickup._core.utils.ApiUtils;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    // 회원가입
    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRequest.RegisterDTO registerDTO){
        userService.register(registerDTO);
        return ResponseEntity.ok(ApiUtils.success("회원가입 성공"));
    }

    // 닉네임 중복 확인
    @GetMapping("/users/duplicate/nickname")
    public ResponseEntity<?> checkNickname(@RequestBody @Valid UserRequest.NicknameCheckDTO nicknameCheckDTO){
        userService.checkNicknameDuplicate(nicknameCheckDTO.getNickname());
        return ResponseEntity.ok(ApiUtils.success("사용 가능한 닉네임 입니다"));
    }

}
