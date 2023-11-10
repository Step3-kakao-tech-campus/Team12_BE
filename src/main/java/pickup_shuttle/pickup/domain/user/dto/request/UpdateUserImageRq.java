package pickup_shuttle.pickup.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UpdateUserImageRq(
        @NotNull(message = "이미지 파일이 없습니다")
        MultipartFile image
) {}
