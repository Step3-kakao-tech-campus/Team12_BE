package pickup_shuttle.pickup.domain.refreshToken.dto.response;

import lombok.Builder;
import pickup_shuttle.pickup.domain.beverage.dto.BeverageDTO;

import java.util.List;

@Builder
public record AccessTokenRpDTO(
        String AccessToken
) { }