package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.ValidValue;
import pickup_shuttle.pickup.domain.beverage.Beverage;
import pickup_shuttle.pickup.domain.beverage.dto.request.BeverageRq;
import pickup_shuttle.pickup.domain.board.Board;
import pickup_shuttle.pickup.domain.store.Store;
import pickup_shuttle.pickup.domain.user.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record CreateBoardRq(
        @NotBlank(message = "가게" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "가게" + ErrorMessage.BADREQUEST_SIZE)
        String shopName,
        @NotEmpty(message = "음료" + ErrorMessage.BADREQUEST_EMPTY)
        List<@Valid BeverageRq> beverages,
        @NotBlank(message = "위치" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "위치" + ErrorMessage.BADREQUEST_SIZE)
        String destination,
        @Min(value = ValidValue.INTEGER_MIN, message = "픽업팁" + ErrorMessage.BADREQUEST_MIN)
        int tip,
        @Size(max = ValidValue.STRING_MAX, message = "요청사항" + ErrorMessage.BADREQUEST_SIZE)
        String request,
        @NotBlank(message = "마감기간" + ErrorMessage.BADREQUEST_BLANK)
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$", message = "마감기간은 yyyy-MM-dd HH:mm 형식이어야 합니다")
        String finishedAt
){

    public Board toBoard(User user, Store store){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(finishedAt, formatter);
        List<Beverage> beverages = beverages(this.beverages);
        Board board = Board.builder()
                .finishedAt(localDateTime)
                .destination(destination)
                .tip(tip)
                .user(user)
                .store(store)
                .beverages(beverages)
                .build();
        board.updateRequest(request);
        return board;
    }

    private List<Beverage> beverages(List<BeverageRq> beverageRqs) {
        return beverageRqs.stream().map(
                        b -> Beverage.builder()
                                .name(b.name())
                                .build())
                .toList();
    }
}
