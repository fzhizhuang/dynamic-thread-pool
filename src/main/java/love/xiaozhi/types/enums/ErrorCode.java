package love.xiaozhi.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author Jason Zong
 * @since 2024/9/9
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "成功"),
    UN_ERROR(-1, "失败"),
    ILLEGAL_PARAMETER(1001, "非法参数");

    private final Integer code;
    private final String desc;
}
