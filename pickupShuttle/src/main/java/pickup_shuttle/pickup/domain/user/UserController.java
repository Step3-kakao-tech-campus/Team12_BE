package pickup_shuttle.pickup.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import pickup_shuttle.pickup._core.utils.ApiUtils;
import pickup_shuttle.pickup.config.Login;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.refreshToken.dto.response.AccessTokenRpDTO;
import pickup_shuttle.pickup.domain.user.dto.request.SignUpRqDTO;
import pickup_shuttle.pickup.security.service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    // 은행명, 계좌번호 입력 창으로 이동
    @GetMapping("/users/register/input")
    public ModelAndView userInfoInput() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("registerInput");

        return modelAndView;
    }

    // 에러창
    @GetMapping("/errorPage")
    public ModelAndView error() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("errorPage");

        return modelAndView;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signup(SignUpRqDTO requestDTO, @AuthenticationPrincipal CustomOauth2User customOauth2User, Errors errors){
        userService.signup(requestDTO, customOauth2User);

        return ResponseEntity.ok(ApiUtils.success("처리에 성공하였습니다." + "은행이름: " + requestDTO.bankName() + "  계좌번호: " + requestDTO.accountNum()));
    }

    @GetMapping("/login/callback")
    public ResponseEntity<?> callBack(Authentication authentication){
        CustomOauth2User customOauth2User = (CustomOauth2User) authentication.getPrincipal();
        if(customOauth2User != null){
            String token = jwtService.createAccessToken(customOauth2User.getName());
            return ResponseEntity.ok().body(ApiUtils.success(AccessTokenRpDTO.builder().AccessToken(token).build()));
        } else {
            return ResponseEntity.badRequest().body(ApiUtils.error("인증에 실패하였습니다.", HttpStatus.UNAUTHORIZED));
        }
    }
    @GetMapping("/mypage/auth")
    public ResponseEntity<?> userAuthStatus(@Login String userId){
        String status = userService.userAuthStatus(Long.parseLong(userId));
        return ResponseEntity.ok(ApiUtils.success(status));
    }

}