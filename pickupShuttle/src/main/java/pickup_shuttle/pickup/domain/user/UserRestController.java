package pickup_shuttle.pickup.domain.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pickup_shuttle.pickup.domain.errors.utils.ApiUtils;
import pickup_shuttle.pickup.domain.user.security.JwtTokenUtil;

@RequiredArgsConstructor
@RestController
public class UserRestController {

    private final UserService userService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.LoginDTO requestDTO, Errors errors){
        String jwt = userService.login(requestDTO);
        return ResponseEntity.ok().header(JwtTokenUtil.HEADER,jwt).body(ApiUtils.success(null));
    }
}
