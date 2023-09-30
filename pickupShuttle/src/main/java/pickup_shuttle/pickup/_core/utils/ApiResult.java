package pickup_shuttle.pickup._core.utils;

public record ApiResult<T>(boolean success, T response, ApiError error) {
}
