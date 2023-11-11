package pickup_shuttle.pickup.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.filter.OncePerRequestFilter;
import pickup_shuttle.pickup._core.errors.exception.Exception400;
import pickup_shuttle.pickup.domain.refreshToken.RefreshToken;
import pickup_shuttle.pickup.domain.refreshToken.RefreshTokenRepository;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.repository.UserRepository;
import pickup_shuttle.pickup.security.service.JwtService;
import pickup_shuttle.pickup.security.util.PasswordUtil;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/api/")||request.getRequestURI().equals("/api/articles")||request.getRequestURI().equals("/api/login/callback") ||request.getRequestURI().equals("/api/signup") || request.getRequestURI().equals("/api/login/oauth2/code/kakao")|| request.getRequestURI().equals("/api/users/register/input")){
            filterChain.doFilter(request,response); // if문에 있는 URI 요청이 들어오면, 다음 필터 호출
            return;  // return으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        System.out.println("JWT Auth 필터 작동중");

        // 엑세스 토큰 검증 (관련 로직은 해당 메서드에 주석으로 기술함.
        checkAccessTokenAndAuthentication(request, response, filterChain);
    }

    /**
     *  [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     *  파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     *  리프레시 토큰과 함께 엑세스 토큰 생성, (주석 처리 한 곳이 무한 리다이렉트 현상 발생, 수정 예정)
     */

    public void checkRefreshTokenAndReIssueAccessToken(HttpServletRequest request,HttpServletResponse response, String refreshToken) throws IOException, ServletException{

        refreshTokenRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user.getRefreshToken());
                    try {
                        jwtService.sendRefreshToken(response,
                                reIssuedRefreshToken);
                        redirectStrategy.sendRedirect(request,response, "/login/callback");
                    } catch (Exception e){
                        new Exception400("해당 리프레시 토큰으로 회원 정보를 찾을 수 없습니다.");
                    }
                });
    }

    /**
     * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드]
     * jwtService.createRefreshToken()으로 리프레시 토큰 재발급 후
     * DB에 재발급한 리프레시 토큰 업데이트 후 Flush
     */

    private String reIssueRefreshToken(String refreshToken) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        Optional<RefreshToken> oldRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        oldRefreshToken.get().updateToken(reIssuedRefreshToken);
        refreshTokenRepository.saveAndFlush(oldRefreshToken.get());
        return reIssuedRefreshToken;
    }

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        if(!jwtService.extractAccessToken(request).isEmpty()) {
            System.out.println("추출된 엑세스 토큰: " + jwtService.extractAccessToken(request).get());
        }
        try {
            Optional<String> accessTokenStr = jwtService.extractAccessToken(request).filter(jwtService::isTokenValid);
            if(accessTokenStr.isEmpty()){
                throw new Exception("유효하지 않은 엑세스 토큰입니다.");
            }
            jwtService.extractAccessToken(request)
                    .filter(jwtService::isTokenValid)
                    .ifPresent(accessToken -> jwtService.extractUserID(accessToken)
                            .ifPresent(userID -> userRepository.findById(Long.valueOf(userID))
                                    .ifPresent(this::saveAuthentication)));
            // 엑세스 토큰 인증되었으면 로그인 처리 (다음 필터 진행)
            System.out.println("엑세스 토큰이 인증되었습니다.");
            filterChain.doFilter(request, response);
        } catch (Exception e){
            // 엑세스 토큰 검증 실패 후 리프레시 토큰 확인
            System.out.println("엑세스 토큰 인증 실패!" + e.getMessage() + " " + e.getStackTrace() + " " + e.getLocalizedMessage());
            String refreshToken = jwtService.extractRefreshToken(request)
                    .filter(jwtService::isTokenValid)
                    .orElse(null);
            if(refreshToken != null) {
                // 리프레시 토큰 유효성 검사 통과 후 JWT 재발급
                checkRefreshTokenAndReIssueAccessToken(request, response, refreshToken);
                filterChain.doFilter(request, response);
            } else {
                // 올바르지 않은 리프레시 토큰에 대한 예외처리 (관련 페이지 리다이렉트)
                throw new Exception400("RefreshToken이 잘못되었거나 만료되었습니다");
            }
        }
    }



    public void saveAuthentication(User myUser) {
        System.out.println("saveAuthentication 실행");
        System.out.println("유저 닉네임: " + myUser.getNickname());
        String password = myUser.getPwd();
        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();
        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getSocialId()) // 식별값인 SocialId를 userDetails의 username에 저장하여 인증처리
                .password(password)
                .roles(myUser.getUserRole().name())
                .build();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        System.out.println("saveAuthentication 인증 성공");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



}
