package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;

@Builder
public record ReadWriterBoardListRp(
        Long boardId,
        String shopName,
        String destination,
        Long finishedAt,
        int tip,
        boolean isMatch
) { }
