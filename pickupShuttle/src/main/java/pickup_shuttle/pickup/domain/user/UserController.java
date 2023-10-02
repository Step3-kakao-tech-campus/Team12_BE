package pickup_shuttle.pickup.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import pickup_shuttle.pickup._core.utils.ApiUtils;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.dto.request.NicknameCheckRqDTO;
import pickup_shuttle.pickup.domain.user.dto.request.RegisterRqDTO;
import pickup_shuttle.pickup.domain.user.dto.request.SignUpRqDTO;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    // 회원가입
    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRqDTO registerDTO){
        userService.register(registerDTO);
        return ResponseEntity.ok(ApiUtils.success("회원가입 성공"));
    }
    
    // 은행명, 계좌번호 입력 창으로 이동
    @GetMapping("/users/register/input")
    public ModelAndView userInfoInput() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registerInput");

        return modelAndView;
    }


    // 닉네임 중복 확인
    @GetMapping("/users/duplicate/nickname")
    public ResponseEntity<?> checkNickname(@RequestBody @Valid NicknameCheckRqDTO nicknameCheckDTO){
        userService.checkNicknameDuplicate(nicknameCheckDTO.nickname());
        return ResponseEntity.ok(ApiUtils.success("사용 가능한 닉네임 입니다"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(SignUpRqDTO requestDTO, @AuthenticationPrincipal CustomOauth2User customOauth2User, Errors errors){
        userService.signup(requestDTO, customOauth2User);

        return ResponseEntity.ok(ApiUtils.success("처리에 성공하였습니다." + "은행이름: " + requestDTO.bankName() + "  계좌번호: " + requestDTO.accountNum()));
    }

}
