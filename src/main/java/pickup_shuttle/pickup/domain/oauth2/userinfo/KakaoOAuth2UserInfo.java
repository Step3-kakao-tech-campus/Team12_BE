package pickup_shuttle.pickup.domain.oauth2.userinfo;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        // System.out.println("getId는: " + attributes.get("id"));
        return String.valueOf(attributes.get("id"));
    }


    @Override
    public String getName() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if(account == null){
            return null;
        }
        return (String) account.get("name");
    }

    @Override
    public String getPhoneNumber() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        if(account == null){
            return null;
        }
        return (String) account.get("phone_number");
    }

    @Override
    public String getNickname() {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        if(account == null || profile == null){
            return null;
        }

        return (String) profile.get("nickname");
    }



}
