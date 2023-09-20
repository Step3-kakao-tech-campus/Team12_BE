package pickup_shuttle.pickup.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
@DynamicUpdate
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "uid", nullable = false)
    private String uid;
    @Column(name = "pwd", nullable = false)
    private String pwd;
    @Column(name = "role", nullable = false)
    @ColumnDefault("일반")
    private String role;
    @Column(name = "nickname", nullable = false)
    private String nickname;
    @Column(name = "url", nullable = false)
    @ColumnDefault("")
    private String url;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    @Column(name = "name", nullable = false)
    private String name;

    @Builder
    public User(String uid, String pwd, String nickname, String phoneNumber, String name) {
        this.uid = uid;
        this.pwd = pwd;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }

    public void updateRole(String role) {
        this.role = role;
    }

    public void updateUrl(String url) {
        this.url = url;
    }
}
