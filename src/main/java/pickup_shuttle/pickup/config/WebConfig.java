package pickup_shuttle.pickup.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserArgument loginUserArgument;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000") // 모든 출처 허용
                        .allowedMethods("OPTIONS","GET", "POST", "DELETE", "PUT", "PATCH") // 모든 HTTP 메서드 허용
                        //       .allowedHeaders("Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization") // 모든 헤더 허용
                        .allowCredentials(true) // 쿠키 인증 요청 허용
                        .exposedHeaders("authorization")
                        .maxAge(100000); // 프리플라이트 요청 캐싱 시간 (초)
            }

        };
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgument);
    }
}