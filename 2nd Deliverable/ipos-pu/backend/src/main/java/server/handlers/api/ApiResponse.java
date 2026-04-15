package server.handlers.api;

public class ApiResponse<T> {
    public boolean success;
    public String code;
    public String message;
    public T data;

    public static <T> ApiResponse<T> ok(String code, String message, T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }
}
