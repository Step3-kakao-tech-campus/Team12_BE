package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;

@Builder
public record  ReadPickerBoardListRp(
        Long boardId,
        String shopName,
        String destination,
        int tip,
        Long finishedAt,
        boolean isMatch
) { }
