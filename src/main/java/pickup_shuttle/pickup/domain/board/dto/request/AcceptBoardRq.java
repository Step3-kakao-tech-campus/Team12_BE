package pickup_shuttle.pickup.domain.board.dto.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record AcceptBoardRq(
      @PositiveOrZero(message = "도착예정 시간이 음수입니다") int arrivalTime
) { }
