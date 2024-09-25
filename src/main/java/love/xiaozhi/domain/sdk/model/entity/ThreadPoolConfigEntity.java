package love.xiaozhi.domain.sdk.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 线程池配置实体
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThreadPoolConfigEntity {

    /* 应用名称 */
    private String applicationName;
    /* 线程池名称 */
    private String threadPoolName;
    /* 当前线程池中线程数 */
    private int poolSize;
    /* 核心线程数 */
    private int corePoolSize;
    /* 最大线程数 */
    private int maxPoolSize;
    /* 当前活跃线程数 */
    private int activeCount;
    /* 队列类型 */
    private String queueType;
    /* 当前队列任务数 */
    private int queueSize;
    /* 队列剩余任务数 */
    private int remainingCapacity;
}
