package love.xiaozhi.infrastructure.config;

import love.xiaozhi.domain.sdk.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.domain.sdk.model.valobj.RegistryEnumVO;
import love.xiaozhi.domain.sdk.service.IDynamicThreadPoolService;
import love.xiaozhi.domain.sdk.service.impl.DynamicThreadPoolService;
import love.xiaozhi.infrastructure.registry.IRegistry;
import love.xiaozhi.infrastructure.registry.impl.RedisRegistry;
import love.xiaozhi.trigger.job.ThreadPoolDataReportJob;
import love.xiaozhi.trigger.listener.ThreadPoolConfigUpdateListener;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池自动配置
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
@Configuration
@EnableScheduling // 开启定时任务
@ConditionalOnClass(DynamicThreadPoolAutoProperties.class)
@EnableConfigurationProperties(DynamicThreadPoolAutoProperties.class)
public class DynamicThreadPoolAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DynamicThreadPoolAutoConfiguration.class);

    private String applicationName;

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
        if (StringUtils.isNotBlank(properties.getPassword())) {
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
    public DynamicThreadPoolService dynamicThreadPoolService(ApplicationContext applicationContext, Map<String, ThreadPoolExecutor> threadPoolExecutorMap, RedissonClient redissonClient) {
        applicationName = applicationContext.getEnvironment().getProperty("spring.application.name");
        if (StringUtils.isBlank(applicationName)) {
            applicationName = "未配置";
            log.warn("动态线程池-SpringBoot应用未配置spring.application.name,无法获取到应用名称");
        }
        // 从注册中心获取数据，配置本地线程池
        Set<String> threadNames = threadPoolExecutorMap.keySet();
        for (String threadName : threadNames) {
            String cacheKey = String.join("_", RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_KEY.getKey(), applicationName, threadName);
            ThreadPoolConfigEntity threadPoolConfig = redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
            if (Objects.isNull(threadPoolConfig)) continue;
            // 更新本地线程池
            ThreadPoolExecutor threadPoolExecutor = threadPoolExecutorMap.get(threadName);
            threadPoolExecutor.setCorePoolSize(threadPoolConfig.getCorePoolSize());
            threadPoolExecutor.setMaximumPoolSize(threadPoolConfig.getMaxPoolSize());
        }
        return new DynamicThreadPoolService(applicationName, threadPoolExecutorMap);
    }

    @Bean("threadPoolConfigUpdateListener")
    public ThreadPoolConfigUpdateListener threadPoolConfigUpdateListener(IDynamicThreadPoolService dynamicThreadPoolService, IRegistry redisRegistry) {
        return new ThreadPoolConfigUpdateListener(dynamicThreadPoolService, redisRegistry);
    }

    @Bean("threadPoolConfigUpdateTopic")
    public RTopic threadPoolConfigUpdateTopic(RedissonClient redissonClient, ThreadPoolConfigUpdateListener threadPoolConfigUpdateListener) {
        String topicKey = String.join("_", RegistryEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey(), applicationName);
        RTopic topic = redissonClient.getTopic(topicKey);
        topic.addListener(ThreadPoolConfigEntity.class, threadPoolConfigUpdateListener);
        return topic;
    }
}
