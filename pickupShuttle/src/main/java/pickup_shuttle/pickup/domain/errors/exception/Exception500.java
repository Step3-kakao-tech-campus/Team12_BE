package pickup_shuttle.pickup.domain.errors.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import pickup_shuttle.pickup.domain.errors.utils.ApiUtils;

@Getter
public class Exception500 extends RuntimeException{
    public Exception500(String message) {
        super(message);
    }
    public ApiUtils.ApiResult<?> body(){
        return ApiUtils.error(getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus status(){
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }


}
