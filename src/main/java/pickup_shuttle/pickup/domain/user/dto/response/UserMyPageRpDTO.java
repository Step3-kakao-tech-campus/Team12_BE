package pickup_shuttle.pickup.domain.user.dto.response;

import lombok.Builder;

@Builder
public record UserMyPageRpDTO(String role, String nickname) { }
