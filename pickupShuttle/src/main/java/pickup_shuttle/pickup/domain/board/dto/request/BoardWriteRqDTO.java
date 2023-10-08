package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record BoardWriteRqDTO(
        @NotBlank(message = "가게가 공백입니다") String store,
        List<String> beverage,
        @NotBlank(message = "위치가 공백입니다") String destination,
        @PositiveOrZero(message = "픽업팁이 음수입니다") int tip,
        String request,
        @NotBlank(message = "마감기간이 공백입니다") String finishedAt
){

    public Board toBoard(User user, Store store){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(finishedAt, formatter);
        List<Beverage> beverageList = beverageList(beverage);
        Board board = Board.builder()
                .finishedAt(localDateTime)
                .destination(destination)
                .tip(tip)
                .user(user)
                .store(store)
                .beverages(beverageList)
                .build();
        board.updateRequest(request);
        return board;
    }

    private List<Beverage> beverageList(List<String> beverage) {
        return beverage.stream().map(
                        b -> Beverage.builder()
                                .name(b)
                                .build())
                .toList();
    }
//    public Beverage toBeverage(Board board){
//        return Beverage.builder()
//                .name()
//                .build();
//        // new Beverage(beverage, board);
//    }

}
