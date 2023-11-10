package pickup_shuttle.pickup.domain.user.dto.response;

import lombok.Builder;

@Builder
public record LoginUserRp(
        String accessToken,
        String nickname,
        String userAuth
) { }
