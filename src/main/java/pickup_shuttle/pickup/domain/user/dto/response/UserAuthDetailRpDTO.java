package pickup_shuttle.pickup.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserAuthDetailRpDTO(
        String nickname,
        String imageUrl
) { }
