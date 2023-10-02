package pickup_shuttle.pickup.domain.match;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "match_tb")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long matchId;

    @Column(name = "arrival_time", nullable = false)
    private int arrivalTime;

    @Column(name = "match_time", nullable = false)
    @CreatedDate
    private LocalDateTime matchTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "board_id")
//    private Board board;

    @Builder
    public Match(int arrivalTime, User user) {
        this.arrivalTime = arrivalTime;
        this.user = user;
    }
}
