package pickup_shuttle.pickup.domain.beverage;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pickup_shuttle.pickup.domain.board.Board;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "beverage_tb")
public class Beverage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long beverageId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;


    @Builder
    public Beverage(String name) {
        this.name = name;
    }

    public void setBoard(Board board) {this.board = board;}

    private void updateName(String name) {
        this.name = name;
    }
}
