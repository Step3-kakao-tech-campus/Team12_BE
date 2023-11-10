package pickup_shuttle.pickup.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_tb")
@DynamicInsert
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "uid", nullable = true)
    private String uid;
    @Column(name = "pwd", nullable = true) // Oauth
    private String pwd;
    @Column(name = "role", nullable = false)
    @ColumnDefault("3")
    private UserRole userRole;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "url", nullable = false)
    @ColumnDefault("''")
    private String url;
    @ColumnDefault("''") // 임시
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;


    // private String refreshToken; // 리프레시 토큰
    private String email; // 처음 OAuth 로그인 한 사람 식별용
    private String socialId; // 카카오 고유 ID로 식별용
    @Column(name = "bank", nullable = false)
    @ColumnDefault("''")
    private String bank;
    @Column(name = "account", nullable = false)
    @ColumnDefault("''")
    private String account;

    @Builder
    public User(String socialId, String email,UserRole userRole, String nickname,
                String phoneNumber,  String bank, String account) {
        this.socialId = socialId;
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
        this.bank = bank;
        this.account = account;
    }

    public void setBank(String bank){
        this.bank = bank;
    }

    public void setAccount(String account){
        this.account = account;
    }

    // 유저 권한 설정 메서드
    public void setRole(UserRole userRole){
        this.userRole = userRole;
    }
    public void authorizeUser(){
        this.userRole = UserRole.USER;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void updateUrl(String url) {
        this.url = url;
    }

    /*public void updateRefreshToken(String updateRefreshToken){
        this.refreshToken = updateRefreshToken;
    }
    */



}