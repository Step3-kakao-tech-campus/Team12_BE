package pickup_shuttle.pickup.domain.oauth2;

import lombok.Builder;
import lombok.Getter;
import pickup_shuttle.pickup.domain.oauth2.userinfo.KakaoOAuth2UserInfo;
import pickup_shuttle.pickup.domain.user.User;
import pickup_shuttle.pickup.domain.user.UserRole;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    // KAKAO OAuth 로그인 진행 시 키가 되는 필드 값, 이는 PK와 같습니다.
    private String nameAttributeKey;
    // KAKAO OAuth 로그인 유저 정보 (닉네임, 이메일, 이름, 프로필사진 등등..)
    private KakaoOAuth2UserInfo kakaoOAuth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, KakaoOAuth2UserInfo kakaoOAuth2UserInfo){
        this.nameAttributeKey = nameAttributeKey;
        this.kakaoOAuth2UserInfo = kakaoOAuth2UserInfo;
    }


    public static OAuthAttributes of(String userNameAttributeName, Map<String, Object> attributes){
        return ofKakao(userNameAttributeName, attributes);
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName ,Map<String, Object> attributes ){
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .kakaoOAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    /**
     * KakaoOAuth2UserInfo에서 socialId(식별값), name, nickname, PhoneNumber 가져와서 build
     * email에는 UUID로 중복 없는 랜덤 값 생성
     * role은 GUEST로 설정
     */
    public User toEntity(KakaoOAuth2UserInfo kakaoOAuth2UserInfo){
        return User.builder()
                .socialId(kakaoOAuth2UserInfo.getId())
                .nickname(kakaoOAuth2UserInfo.getNickname())
                .phoneNumber(kakaoOAuth2UserInfo.getPhoneNumber())
                .userRole(UserRole.GUEST) // JWT 발급용
                .email(UUID.randomUUID() + "@kakao.com") // JWT Token 발급용
                .build();
    }


}