package pickup_shuttle.pickup.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup.security.service.JwtService;

@Component
@RequiredArgsConstructor

public class LoginUserArgument implements HandlerMethodArgumentResolver {
    private final JwtService jwtService;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @Login 어노테이션이 붙어있어야 하고
        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        // @Login이 붙은 것은 타입이 Member 클래스여야 한다.
        boolean hasStringType = String.class.isAssignableFrom(parameter.getParameterType());
        return hasLoginAnnotation && hasStringType;
    }
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = jwtService.extractAccessToken(request).orElseThrow(
                () -> new Exception400("access token을 추출하지 못했습니다")
        );
        String userId = jwtService.extractUserID(accessToken).orElseThrow(
                () -> new Exception400("user의 Id를 추출하지 못했습니다")
        );
        return userId;
    }
}

