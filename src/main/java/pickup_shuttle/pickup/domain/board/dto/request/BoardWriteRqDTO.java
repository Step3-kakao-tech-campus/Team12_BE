package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record BoardWriteRqDTO(
        @NotBlank(message = "가게" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "가게" + ErrorMessage.BADREQUEST_SIZE)
        String store,
        List<@NotBlank(message = "음료" + ErrorMessage.BADREQUEST_BLANK)String> beverage,
        @NotBlank(message = "위치" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "위치" + ErrorMessage.BADREQUEST_SIZE)
        String destination,
        @NotNull(message = "픽업팁" + ErrorMessage.BADREQUEST_BLANK)
        @Min(value = 1, message = "픽업팁" + ErrorMessage.BADREQUEST_MIN)
        @Max(value = Integer.MAX_VALUE, message = "픽업팁" + ErrorMessage.BADREQUEST_MAX)
        Integer tip,
        @Size(max = 60, message = "요청사항" + ErrorMessage.BADREQUEST_SIZE)
        String request,
        @NotBlank(message = "마감기간" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = 60, message = "마감기간" + ErrorMessage.BADREQUEST_SIZE)
        String finishedAt
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
