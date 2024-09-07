package love.xiaozhi.middleware.thread.sdk.trigger.listener;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import love.xiaozhi.middleware.thread.sdk.domain.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.middleware.thread.sdk.domain.service.IDynamicThreadPoolService;
import love.xiaozhi.middleware.thread.sdk.registry.IRegistry;
import org.redisson.api.listener.MessageListener;

import java.util.List;

/**
 * 线程池配置变更监听器
 *
 * @author Jason Zong
 * @since 2024/9/8
 */
@Slf4j
public class ThreadPoolConfigUpdateListener implements MessageListener<ThreadPoolConfigEntity> {

    private final IDynamicThreadPoolService dynamicThreadPoolService;
    private final IRegistry registry;

    public ThreadPoolConfigUpdateListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Override
    public void onMessage(CharSequence charSequence, ThreadPoolConfigEntity threadPoolConfigEntity) {
        log.info("动态线程池-监听变更线程池配置 线程池名称:{} 线程池核心数:{} 最大线程数:{}", threadPoolConfigEntity.getThreadPoolName(), threadPoolConfigEntity.getCorePoolSize(), threadPoolConfigEntity.getMaxPoolSize());
        dynamicThreadPoolService.updateThreadPoolConfig(threadPoolConfigEntity);
        // 更新后上报最新数据
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.listThreadPoolConfigs();
        registry.reportThreadPoolConfig(threadPoolConfigEntities);
        log.info("动态线程池-变更线程池,上报最新线程池配置列表 {}", JSON.toJSONString(threadPoolConfigEntities));
        ThreadPoolConfigEntity threadPoolConfig = dynamicThreadPoolService.queryThreadPoolConfigByName(threadPoolConfigEntity.getThreadPoolName());
        registry.reportThreadPoolConfigParameter(threadPoolConfig);
        log.info("动态线程池-变更线程池,上报最新线程池配置 线程池名称:{} 配置信息:{}", threadPoolConfig.getThreadPoolName(), JSON.toJSONString(threadPoolConfig));
    }
}
