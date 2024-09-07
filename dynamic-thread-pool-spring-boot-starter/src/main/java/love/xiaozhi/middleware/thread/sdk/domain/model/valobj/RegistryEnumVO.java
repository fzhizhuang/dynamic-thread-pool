package love.xiaozhi.middleware.thread.sdk.domain.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 注册中心枚举值对象
 *
 * @author Jason Zong
 * @since 2024/9/8
 */
@Getter
@AllArgsConstructor
public enum RegistryEnumVO {

    THREAD_POOL_CONFIG_LIST_KEY("THREAD_POOL_CONFIG_LIST_KEY", "池化配置列表"),
    THREAD_POOL_CONFIG_PARAMETER_KEY("THREAD_POOL_CONFIG_PARAMETER_KEY", "池化配置参数"),
    DYNAMIC_THREAD_POOL_REDIS_TOPIC("DYNAMIC_THREAD_POOL_REDIS_TOPIC", "动态线程池监听主题配置");
    private final String key;
    private final String desc;
}
