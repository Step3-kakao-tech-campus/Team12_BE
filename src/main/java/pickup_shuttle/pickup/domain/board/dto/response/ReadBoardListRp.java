package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;

@Builder
public record ReadBoardListRp(
    Long boardId,
    String shopName,
    Long finishedAt,
    int tip,
    boolean isMatch,
    String destination
) {}

