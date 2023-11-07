package pickup_shuttle.pickup.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserPickerListRpDTO(
        Long boardId,
        String shopName,
        String destination,
        int tip,
        Long finishedAt,
        boolean isMatch
) { }
