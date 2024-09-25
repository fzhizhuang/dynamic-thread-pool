package love.xiaozhi.trigger.job;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import love.xiaozhi.domain.sdk.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.domain.sdk.service.IDynamicThreadPoolService;
import love.xiaozhi.infrastructure.registry.IRegistry;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 线程池数据定时上报任务
 *
 * @author Jason Zong
 * @since 2024/9/8
 */
@Slf4j
public class ThreadPoolDataReportJob {

    private final IDynamicThreadPoolService dynamicThreadPoolService;
    private final IRegistry registry;

    public ThreadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry registry) {
        this.dynamicThreadPoolService = dynamicThreadPoolService;
        this.registry = registry;
    }

    @Scheduled(cron = "0/20 * * * * *")
    public void execReportThreadPoolList() {
        // 获取线程配置信息列表
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.listThreadPoolConfigs();
        // 上报配置列表
        registry.reportThreadPoolConfig(threadPoolConfigEntities);
        log.info("动态线程池定时上报-线程池信息:{}", JSON.toJSONString(threadPoolConfigEntities));
    }

    @Scheduled(cron = "0/20 * * * * *")
    public void execReportThreadPoolConfig() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = dynamicThreadPoolService.listThreadPoolConfigs();
        threadPoolConfigEntities.forEach(threadPoolConfig -> {
            registry.reportThreadPoolConfigParameter(threadPoolConfig);
            log.info("动态线程池定时上报-线程池配置:{}", JSON.toJSONString(threadPoolConfig));
        });
    }

}
