package server.handlers.api;

/**
 * Standard API response wrapper.
 *
 * @param <T> the response data type
 */
public class ApiResponse<T> {
    public boolean success;
    public String code;
    public String message;
    public T data;

    /**
     * Creates a successful API response.
     *
     * @param code the response code
     * @param message the response message
     * @param data the response payload
     * @param <T> the payload type
     * @return a success response
     */
    public static <T> ApiResponse<T> ok(String code, String message, T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.code = code;
        r.message = message;
        r.data = data;
        return r;
    }

    /**
     * Creates an error API response.
     *
     * @param code the error code
     * @param message the error message
     * @param <T> the payload type
     * @return an error response
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }
}
