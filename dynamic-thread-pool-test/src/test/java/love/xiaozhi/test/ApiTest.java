package love.xiaozhi.test;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import love.xiaohzi.middleware.Application;
import love.xiaozhi.middleware.thread.sdk.domain.model.entity.ThreadPoolConfigEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RTopic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

/**
 * 测试
 *
 * @author Jason Zong
 * @since 2024/9/7
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApiTest {

    @Resource
    private RTopic threadPoolConfigUpdateTopic;

    @SneakyThrows
    @Test
    public void test() {
        ThreadPoolConfigEntity threadPoolConfig = ThreadPoolConfigEntity.builder()
                .applicationName("dynamic-thread-pool-test")
                .threadPoolName("threadPoolExecutor01")
                .corePoolSize(100)
                .maxPoolSize(200)
                .build();
        threadPoolConfigUpdateTopic.publish(threadPoolConfig);
        new CountDownLatch(1).await();
    }
}
