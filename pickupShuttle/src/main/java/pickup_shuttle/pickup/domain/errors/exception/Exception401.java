package pickup_shuttle.pickup.domain.errors.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pickup_shuttle.pickup.domain.errors.utils.ApiUtils;

// 인증이 되지 않았을 시
@Getter
public class Exception401 extends RuntimeException{
    public Exception401(String message){
        super(message);
    }

    public ApiUtils.ApiResult<?> body() {
        return ApiUtils.error(getMessage(), HttpStatus.UNAUTHORIZED);
    }

    public HttpStatus status() {
        return HttpStatus.UNAUTHORIZED;
    }
}
