package pickup_shuttle.pickup.domain.refreshToken;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pickup_shuttle.pickup.domain.user.User;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public RefreshToken(String token, User user) {
        this.refreshToken = token;
        this.user = user;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }

    @Builder
    public RefreshToken(User user, String token){
        this.user = user;
        this.refreshToken = token;
    }
}
