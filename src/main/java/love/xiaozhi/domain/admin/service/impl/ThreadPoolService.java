package love.xiaozhi.domain.admin.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import love.xiaozhi.domain.admin.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.domain.admin.model.valobj.KeyEnumVO;
import love.xiaozhi.domain.admin.service.IThreadPoolService;
import love.xiaozhi.trigger.dto.UpdateThreadPoolDTO;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 线程池服务实现
 *
 * @author Jason Zong
 * @since 2024/9/9
 */
@Slf4j
@Service
public class ThreadPoolService implements IThreadPoolService {

    @Resource
    private RedissonClient redissonClient;

    @Override
    public List<ThreadPoolConfigEntity> listThreadPoolConfigs() {
        return redissonClient.<ThreadPoolConfigEntity>getList(KeyEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey())
                .readAll();
    }

    @Override
    public ThreadPoolConfigEntity queryThreadPoolConfig(String applicationName, String threadPoolName) {
        String cacheKey = String.join("_", KeyEnumVO.THREAD_POOL_CONFIG_PARAMETER_KEY.getKey(), applicationName, threadPoolName);
        return redissonClient.<ThreadPoolConfigEntity>getBucket(cacheKey).get();
    }

    @Override
    public void updateThreadPoolConfig(UpdateThreadPoolDTO updateThreadPoolDTO) {
        ThreadPoolConfigEntity threadPoolConfigEntity = ThreadPoolConfigEntity.builder()
                .corePoolSize(updateThreadPoolDTO.getCorePoolSize())
                .maxPoolSize(updateThreadPoolDTO.getMaxPoolSize())
                .build();
        String topicKey = String.join("_", KeyEnumVO.DYNAMIC_THREAD_POOL_REDIS_TOPIC.getKey(), updateThreadPoolDTO.getApplicationName());
        RTopic topic = redissonClient.getTopic(topicKey);
        topic.publish(threadPoolConfigEntity);
    }
}
