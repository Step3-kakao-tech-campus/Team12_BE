package pickup_shuttle.pickup.domain.oauth2.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pickup_shuttle.pickup.domain.oauth2.CustomOauth2User;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRole;
import pickup_shuttle.pickup.domain.user.repository.UserRepository;
import pickup_shuttle.pickup.security.service.JwtService;

import java.io.IOException;

@Log4j2
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        System.out.println("로그인 성공되었습니다!");
        CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
        System.out.println("loginSuccess 실행");
        loginSuccess(response, oauth2User);
        redirectStrategy.sendRedirect(request, response, "https://k0d01653e1a11a.user-app.krampoline.com/login/callback");
    }

    private void loginSuccess(HttpServletResponse response, CustomOauth2User oauth2User) throws IOException {
        String userPK = String.valueOf(userRepository.findBySocialId(oauth2User.getName()).get().getUserId());
        System.out.println("리프레시 토큰과 엑세스 토큰 발행!, userPK: " + userPK);
        String accessToken = null; // 엑세스 토큰은 "/login/callback"에서 응답 Body 형태로 응답해줄 예정.
        String refreshToken = jwtService.createRefreshToken();

        jwtService.sendRefreshToken(response, refreshToken);
        jwtService.updateRefreshToken(oauth2User.getName(), refreshToken);
    }



}