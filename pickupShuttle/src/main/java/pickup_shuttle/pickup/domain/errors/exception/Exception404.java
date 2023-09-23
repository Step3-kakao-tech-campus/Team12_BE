package pickup_shuttle.pickup.domain.errors.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pickup_shuttle.pickup.domain.errors.utils.ApiUtils;

// 권한 없음.
@Getter
public class Exception404 extends RuntimeException {
    public Exception404(String message) {
        super(message);
    }
    public ApiUtils.ApiResult<?> body(){
        return ApiUtils.error(getMessage(), HttpStatus.NOT_FOUND);
    }

    public HttpStatus status(){
        return HttpStatus.NOT_FOUND;
    }

}
