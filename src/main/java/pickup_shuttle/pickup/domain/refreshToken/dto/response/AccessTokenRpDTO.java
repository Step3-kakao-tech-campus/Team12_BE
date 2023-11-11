package pickup_shuttle.pickup.domain.refreshToken.dto.response;

import lombok.Builder;

@Builder
public record AccessTokenRpDTO(
        String AccessToken
) { }