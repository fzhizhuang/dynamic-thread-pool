package love.xiaozhi.middleware.thread.sdk.domain.service;

import love.xiaozhi.middleware.thread.sdk.domain.model.entity.ThreadPoolConfigEntity;

import java.util.List;

/**
 * 动态线程池服务 获取线程池数据
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
public interface IDynamicThreadPoolService {

    /**
     * 查询所有的线程池配置
     *
     * @return 线程池配置列表
     */
    List<ThreadPoolConfigEntity> listThreadPoolConfigs();

    /**
     * 根据线程名查询线程配置
     *
     * @param threadPoolName 线程名
     * @return 线程配置
     */
    ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName);

    /**
     * 更新线程池配置
     *
     * @param threadPoolConfigEntity 线程池配置
     */
    void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity);
}
