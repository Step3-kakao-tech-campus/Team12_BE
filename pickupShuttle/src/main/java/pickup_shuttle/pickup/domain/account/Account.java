package pickup_shuttle.pickup.domain.account;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pickup_shuttle.pickup.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_tb")
public class Account {
    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "number", nullable = false)
    private String number;
    @Column(name = "bank", nullable = false)
    private String bank;

    @Builder
    public Account(User user, String number, String bank) {
        this.user = user;
        this.number = number;
        this.bank = bank;
    }

}