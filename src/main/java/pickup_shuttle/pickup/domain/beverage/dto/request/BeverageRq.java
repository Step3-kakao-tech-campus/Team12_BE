package pickup_shuttle.pickup.domain.beverage.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import pickup_shuttle.pickup.config.ErrorMessage;
import pickup_shuttle.pickup.config.ValidValue;

@Builder
public record BeverageRq(
        @NotBlank(message = "음료" + ErrorMessage.BADREQUEST_BLANK)
        @Size(max = ValidValue.STRING_MAX, message = "음료" + ErrorMessage.BADREQUEST_SIZE)
        String name
) { }