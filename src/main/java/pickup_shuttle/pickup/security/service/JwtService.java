package pickup_shuttle.pickup.security.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pickup_shuttle.pickup.domain.refreshToken.RefreshToken;
import pickup_shuttle.pickup.domain.refreshToken.RefreshTokenRepository;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRepository;

import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Getter
@Log4j2
@Service
@Transactional(readOnly = true)
@PropertySource("classpath:application-dev-oauth.yml")
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod; // 만료시간 30분


    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod; // 만료시간 2주

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    /**
     * JWT의 Subject와 Claim으로 email 사용 -> 클레임의 name을 "userPKID"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String userPKid = "userPKid";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public String createAccessToken(String userid) {
        Date now = new Date();

        return JWT.create() // JWT 토큰을 생성하는 빌더 반환
                .withSubject(ACCESS_TOKEN_SUBJECT) // JWT의 Subject 지정 -> AccessToken이므로 AccessToken
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod)) // 토큰 만료 시간 설정

                //클레임으로는 저희는 PK 하나만 사용합니다.
                .withClaim(userPKid, userid)
                .sign(Algorithm.HMAC512(secretKey)); // HMAC512 알고리즘 사용, application-jwt.properties에서 지정한 secret 키로 암호화
    }

    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }


    /**
     * Refresh Token, 쿠키로 구워줌.
     */
    public void sendRefreshToken(HttpServletResponse response, String refreshToken){
        response.setStatus(HttpServletResponse.SC_OK);
        Cookie refreshCookie = new Cookie("refresh_token",refreshToken);
        refreshCookie.setMaxAge(60*60*24*14);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        response.addCookie(refreshCookie);
        log.info("Refresh Token 쿠키 발급 완료");
    }

    /**
     * 쿠키에서 RefreshToken 추출
     */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies=request.getCookies(); // 모든 쿠키 가져오기
        String refreshtokenStr = null;
        if(cookies!=null){
            for (Cookie c : cookies) {
                String name = c.getName(); // 쿠키 이름 가져오기
                String value = c.getValue(); // 쿠키 값 가져오기
                if (name.equals("refresh_token")) {
                    refreshtokenStr = value;
                    System.out.println("추출한 refreshTokenStr: " + refreshtokenStr);
                }
            }
        }
        return Optional.ofNullable(refreshtokenStr);
    }

    /**
     * 헤더에서 AccessToken 추출
     * 토큰 형식 : Bearer XXX에서 Bearer를 제외하고 순수 토큰만 가져오기 위해서
     * 헤더를 가져온 후 "Bearer"를 삭제(""로 replace)
     */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * AccessToken에서 userPK 추출
     * 추출 전에 JWT.require()로 검증기 생성
     * verify로 AceessToken 검증 후
     * 유효하다면 getClaim()으로 userPK 추출
     * 유효하지 않다면 빈 Optional 객체 반환
     */
    public Optional<String> extractUserID(String accessToken) {
        try {
            // 토큰 유효성 검사하는 데에 사용할 알고리즘이 있는 JWT verifier builder 반환
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                    .build() // 반환된 빌더로 JWT verifier 생성
                    .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생
                    .getClaim(userPKid) // claim(PK) 가져오기
                    .asString());
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }


    /**
     * RefreshToken DB 저장(업데이트)
     */
    public void updateRefreshToken(String socialId, String refreshToken) {
        Optional<User> user = userRepository.findBySocialId(socialId);
        System.out.println("user의 pk ID 값: "  + user.get().getUserId().toString());
        RefreshToken myRefreshToken = RefreshToken.builder().user(user.get()).token(refreshToken).build();
        refreshTokenRepository.findByUserId(Long.valueOf(user.get().getUserId().toString()))
                .ifPresentOrElse(
                        refresh -> refresh.updateToken(refreshToken),
                        () ->  refreshTokenRepository.save(myRefreshToken));/*new Exception("일치하는 회원이 없습니다.")); */
    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            System.out.println("유효한 토큰입니다!");
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }




}
