package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Getter;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class WriteRqDTO {
    @NotBlank(message = "가게가 공백입니다")
    private String store;
    @NotBlank(message = "음료가 공백입니다")
    private String beverage;
    @NotBlank(message = "위치가 공백입니다")
    private String destination;
    @PositiveOrZero(message = "픽업팁이 음수입니다")
    private int tip;
    private String request;
    @NotBlank(message = "마감기간이 공백입니다")
    private String finishAt;

    public Board toBoard(User user, Store store){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(finishAt, formatter);
        Board board = Board.builder()
                .finishAt(localDateTime)
                .destination(destination)
                .tip(tip)
                .user(user)
                .store(store)
                .build();
        board.updateRequest(request);
        return board;
    }

    public Beverage toBeverage(Board board){
        return Beverage.builder()
                .name(beverage)
                .board(board)
                .build();
    }

}
