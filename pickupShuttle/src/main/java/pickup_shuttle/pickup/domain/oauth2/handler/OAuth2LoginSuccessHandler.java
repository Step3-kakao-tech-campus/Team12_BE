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
import pickup_shuttle.pickup.domain.user.UserRepository;
import pickup_shuttle.pickup.domain.user.UserRole;
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
        try {
            CustomOauth2User oauth2User = (CustomOauth2User) authentication.getPrincipal();
            // User의 Role이 Guest일 경우에 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if(oauth2User.getUserRole() == UserRole.GUEST){
                //String loginId, String key, long expireTimeMs
                String accessToken = jwtService.createAccessToken(oauth2User.getName());
                response.addHeader(jwtService.getAccessHeader(),"Bearer " + accessToken);
                response.sendRedirect("/users/register/input"); // 리다이렉트 주소 (계좌번호 입력)
                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
                User findUser = userRepository.findBySocialId(oauth2User.getName())
                  .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
                findUser.authorizeUser();

            } else {
                System.out.println("loginSuccess 실행");
                loginSuccess(response, oauth2User);
                redirectStrategy.sendRedirect(request, response, "/");
            }
        } catch(Exception e){
            throw e;
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOauth2User oauth2User) throws IOException {
        System.out.println("리프레시 토큰과 엑세스 토큰 발행!");
        String accessToken = jwtService.createAccessToken(oauth2User.getName());
        String refreshToken = jwtService.createRefreshToken();
        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oauth2User.getName(), refreshToken);
    }



}
