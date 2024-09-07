package love.xiaozhi.middleware.thread.sdk.config;

import cn.hutool.core.util.StrUtil;
import love.xiaozhi.middleware.thread.sdk.domain.service.impl.DynamicThreadPoolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态线程池自动配置
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
@Configuration
public class DynamicThreadPoolAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DynamicThreadPoolAutoConfiguration.class);

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
