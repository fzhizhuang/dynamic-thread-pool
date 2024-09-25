package love.xiaozhi.types.res;

import lombok.Data;
import love.xiaozhi.types.enums.ErrorCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * api 响应
 *
 * @author Jason Zong
 * @since 2024/9/9
 */
@Data
public class ApiResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String message;
    private T data;


    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(ErrorCode.SUCCESS.getCode());
        response.setMessage(ErrorCode.SUCCESS.getDesc());
        response.setData(data);
        return response;
    }

    public static <T> ApiResponse<T> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getDesc());
        return response;
    }
}
