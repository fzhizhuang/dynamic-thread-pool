package love.xiaozhi.middleware.thread.sdk.config;

import cn.hutool.core.util.StrUtil;
import love.xiaozhi.middleware.thread.sdk.domain.service.IDynamicThreadPoolService;
import love.xiaozhi.middleware.thread.sdk.domain.service.impl.DynamicThreadPoolService;
import love.xiaozhi.middleware.thread.sdk.registry.IRegistry;
import love.xiaozhi.middleware.thread.sdk.registry.impl.RedisRegistry;
import love.xiaozhi.middleware.thread.sdk.trigger.job.ThreadPoolDataReportJob;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池自动配置
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
@Configuration
@EnableScheduling // 开启定时任务
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
public class DynamicThreadPoolAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DynamicThreadPoolAutoConfiguration.class);

    @Bean("redissonClient")
    public RedissonClient redissonClient(DynamicThreadPoolAutoProperties properties) {
        Config config = new Config();
        //使用json序列化方式
        config.setCodec(JsonJacksonCodec.INSTANCE);
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive());
        if (StrUtil.isNotBlank(properties.getPassword())) {
            singleServerConfig.setPassword(properties.getPassword());
        }
        RedissonClient redissonClient = Redisson.create(config);
        log.info("动态线程池-注册器连接初始化完成. {} {} {}", properties.getHost(), properties.getPort(), !redissonClient.isShutdown());
        return redissonClient;
    }


    @Bean("redisRegistry")
    public IRegistry redisRegistry(RedissonClient redissonClient) {
        return new RedisRegistry(redissonClient);
    }


    @Bean("threadPoolDataReportJob")
    public ThreadPoolDataReportJob threadPoolDataReportJob(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry redisRegistry) {
        return new ThreadPoolDataReportJob(dynamicThreadPoolService, redisRegistry);
    }

    @Bean("dynamicThreadPoolService")
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap) {
        String applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StrUtil.isBlank(applicationName)) {
            applicationName = "未配置";
            log.warn("动态线程池-SpringBoot应用未配置spring.application.name,无法获取到应用名称");
        }
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }
}
