package pickup_shuttle.pickup.domain.user.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {

    // JWT Token 발급
    public static String createToken(String loginId, String key, long expireTimeMs) {
        // Claim = Gwt Token에 들어갈 정보
        // Claim에 LoginId를 넣어 줌으로써 나중에 LoginId를 꺼낼 수 있다.
        Claims claims = Jwts.claims();
        claims.put("loginId", loginId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    // Claims에서 LoginId 꺼내기
    public static String getLoginId(String token, String secretKey) {
        return extractClaims(token,secretKey).get("loginId").toString();
    }

    // 발급된 Token이 만료 시간이 지났는지 체크
    public static boolean isExpired(String token, String secretKey) {
        Date expiredDate = extractClaims(token, secretKey).getExpiration();
        // Token의 만료 날짜가 지금보다 이전인지 check
        return expiredDate.before(new Date());
    }

    // SecretKey를 사용하여 토큰을 파싱.
    private static Claims extractClaims(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

}
