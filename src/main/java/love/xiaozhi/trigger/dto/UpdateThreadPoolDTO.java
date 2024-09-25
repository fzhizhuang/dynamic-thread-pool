package love.xiaozhi.trigger.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新线程池DTO
 *
 * @author Jason Zong
 * @since 2024/9/9
 */
@Data
public class UpdateThreadPoolDTO {

    @NotBlank(message = "应用名必填")
    private String applicationName;

    @NotBlank(message = "线程池名称")
    private String threadPoolName;

    @NotNull(message = "核心线程数必填")
    @Min(value = 1L, message = "核心线程必须大于0")
    private int corePoolSize;

    @NotNull(message = "最大线程数必填")
    @Min(value = 1L, message = "最大线程必须大于0")
    private int maxPoolSize;
}
