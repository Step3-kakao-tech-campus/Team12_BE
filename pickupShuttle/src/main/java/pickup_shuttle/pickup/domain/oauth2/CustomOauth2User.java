package pickup_shuttle.pickup.domain.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import pickup_shuttle.pickup.domain.user.UserRole;

import java.util.Collection;
import java.util.Map;

@Setter
@Getter
public class CustomOauth2User extends DefaultOAuth2User {

    // getName으로 가져온 정보는 USER 엔티티의 SocialID랑 같다.
    private String email;
    private String bankName;
    private String accountNum;
    private UserRole userRole;

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,String email , String accountNum, UserRole userRole) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.accountNum = accountNum;
        this.userRole = userRole;
    }

}
