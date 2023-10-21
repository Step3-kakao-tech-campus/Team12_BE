package pickup_shuttle.pickup.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserAuthListRpDTO(Long userId, String name) {
}
