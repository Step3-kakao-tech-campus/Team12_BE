package pickup_shuttle.pickup.domain.board;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pickup_shuttle.pickup.domain.match.Match;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.user.User;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board_tb")
@DynamicInsert
public class Board {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(name = "finished_at", nullable = false)
    private LocalDateTime finishAt;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "tip", nullable = false)
    private int tip;

    @Column(name = "request", nullable = false)
    @ColumnDefault("''")
    private String request;

    @Column(name = "is_match", nullable = false)
    @ColumnDefault("'N'")
    private boolean isMatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @Builder
    public Board(LocalDateTime finishAt, String destination, int tip, User user, Store store) {
        this.finishAt = finishAt;
        this.destination = destination;
        this.tip = tip;
        this.user = user;
        this.store = store;
    }

    public void updateRequest(String request) {
        this.request = request;
    }

    public void updateMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }
}
