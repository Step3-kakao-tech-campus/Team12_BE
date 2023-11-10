package pickup_shuttle.pickup.domain.oauth2.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler  {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("소셜 로그인 실패!");
        log.info("소셜 로그인에 실패했습니다. 에러메시지: {}" , exception.getMessage());
    }

}
