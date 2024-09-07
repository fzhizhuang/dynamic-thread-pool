package love.xiaozhi.middleware.thread.sdk.domain.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import love.xiaozhi.middleware.thread.sdk.domain.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.middleware.thread.sdk.domain.service.IDynamicThreadPoolService;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池服务实现
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
@Slf4j
public class DynamicThreadPoolService implements IDynamicThreadPoolService {

    private final String applicationName;
    private final Map<String, ThreadPoolExecutor> threadPoolExecutorMap;

    public DynamicThreadPoolService(String applicationName, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        this.applicationName = applicationName;
        this.threadPoolExecutorMap = threadPoolExecutorMap;
    }

    @Override
    public List<ThreadPoolConfigEntity> listThreadPoolConfigs() {
        Set<String> threadPoolKeys = threadPoolExecutorMap.keySet();
        List<ThreadPoolConfigEntity> threadPoolConfigList = new ArrayList<>(threadPoolKeys.size());
        threadPoolKeys.forEach(threadPool -> {
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPool);
            ThreadPoolConfigEntity threadPoolConfigEntity = ThreadPoolConfigEntity.builder()
                    .applicationName(applicationName)
                    .threadPoolName(threadPool)
                    .poolSize(threadPoolExecutor.getPoolSize())
                    .corePoolSize(threadPoolExecutor.getCorePoolSize())
                    .maxPoolSize(threadPoolExecutor.getMaximumPoolSize())
                    .activeCount(threadPoolExecutor.getActiveCount())
                    .queueType(threadPoolExecutor.getQueue().getClass().getSimpleName())
                    .queueSize(threadPoolExecutor.getQueue().size())
                    .remainingCapacity(threadPoolExecutor.getQueue().remainingCapacity())
                    .build();
            threadPoolConfigList.add(threadPoolConfigEntity);
        });
        return threadPoolConfigList;
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfigByName(String threadPoolName) {
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolName);
        if (Objects.isNull(threadPoolExecutor)) return ThreadPoolConfigEntity.builder()
                .applicationName(applicationName)
                .threadPoolName(threadPoolName)
                .build();
        ThreadPoolConfigEntity threadPoolConfigEntity = ThreadPoolConfigEntity.builder()
                .applicationName(applicationName)
                .threadPoolName(threadPoolName)
                .poolSize(threadPoolExecutor.getPoolSize())
                .corePoolSize(threadPoolExecutor.getCorePoolSize())
                .maxPoolSize(threadPoolExecutor.getMaximumPoolSize())
                .activeCount(threadPoolExecutor.getActiveCount())
                .queueType(threadPoolExecutor.getQueue().getClass().getSimpleName())
                .queueSize(threadPoolExecutor.getQueue().size())
                .remainingCapacity(threadPoolExecutor.getQueue().remainingCapacity())
                .build();
        if (log.isDebugEnabled()) {
            log.info("动态线程池-配置查询 应用名:{} 线程名:{} 池化配置:{}", threadPoolName, threadPoolName, JSON.toJSONString(threadPoolConfigEntity));
        }
        return threadPoolConfigEntity;
    }

    @Override
    public void updateThreadPoolConfig(ThreadPoolConfigEntity threadPoolConfigEntity) {
        if (Objects.isNull(threadPoolConfigEntity) || !applicationName.equals(threadPoolConfigEntity.getApplicationName()))
            return;
        ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadPoolConfigEntity.getThreadPoolName());
        if (Objects.isNull(threadPoolExecutor)) return;
        // 设置参数, 调整核心线程数和最大线程数
        threadPoolExecutor.setCorePoolSize(threadPoolConfigEntity.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolConfigEntity.getMaxPoolSize());
    }
}
