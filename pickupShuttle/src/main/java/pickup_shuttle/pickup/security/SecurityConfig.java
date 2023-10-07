package pickup_shuttle.pickup.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import pickup_shuttle.pickup.domain.oauth2.handler.OAuth2LoginFailureHandler;
import pickup_shuttle.pickup.domain.oauth2.handler.OAuth2LoginSuccessHandler;
import pickup_shuttle.pickup.domain.oauth2.service.CustomOAuth2UserService;
import pickup_shuttle.pickup.domain.refreshToken.RefreshTokenRepository;
import pickup_shuttle.pickup.domain.user.UserRepository;
import pickup_shuttle.pickup.security.filter.JwtAuthenticationProcessingFilter;
import pickup_shuttle.pickup.security.service.JwtService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                .headers((headers) -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/**"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/images/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/profile")
                        ).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/users/register/input")).hasAuthority("ROLE_GUEST")
                        .requestMatchers(new AntPathRequestMatcher("/admin")).hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated())

                .logout((logout) -> logout
                        .logoutSuccessUrl("/"))

                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService))
                        .defaultSuccessUrl("/", true)
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                )

                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true));

        http.addFilterBefore(jwtAuthenticationProcessingFilter(), OAuth2LoginAuthenticationFilter.class);

        //http.authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/**")).permitAll().anyRequest().authenticated())

        return http.build();

    }


    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }


    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(jwtService,userRepository,refreshTokenRepository);
        return jwtAuthenticationProcessingFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
