package pickup_shuttle.pickup.domain.board.dto.response;

import lombok.Builder;

@Builder
public record BoardDetailBeforeRpDTO(
        Long boardId,
        String shopName,
        String destination,
        int tip,
        String request,
        Long finishedAt,
        boolean isMatch
) { }
