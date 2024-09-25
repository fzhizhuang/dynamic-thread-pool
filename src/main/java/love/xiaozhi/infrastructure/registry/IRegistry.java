package love.xiaozhi.infrastructure.registry;

import love.xiaozhi.domain.sdk.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 注册中心接口 用于上报数据
 *
 * @author Jason Zong
 * @since 2024/9/8
 */
public interface IRegistry {

    /**
     * 上报线程池
     *
     * @param threadPoolConfigEntities 线程池配置信息列表
     */
    void reportThreadPoolConfig(List<ThreadPoolConfigEntity> threadPoolConfigEntities);

    /**
     * 上报线程池配置参数
     *
     * @param threadPoolConfigEntity 线程池配置参数
     */
    void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity);
}
