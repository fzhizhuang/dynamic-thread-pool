package love.xiaozhi.infrastructure.registry.impl;

import love.xiaozhi.domain.sdk.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.domain.sdk.model.valobj.RegistryEnumVO;
import love.xiaozhi.infrastructure.registry.IRegistry;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.List;

/**
 * Redis实现注册中心
 *
 * @author Jason Zong
 * @since 2024/9/8
 */
public class RedisRegistry implements IRegistry {

    private final RedissonClient redissonClient;

    public RedisRegistry(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public void reportThreadPoolConfig(List<ThreadPoolConfigEntity> threadPoolConfigEntities) {
        RList<ThreadPoolConfigEntity> list = redissonClient.getList(RegistryEnumVO.THREAD_POOL_CONFIG_LIST_KEY.getKey());
        list.addAll(threadPoolConfigEntities);
    }

    @Override
    public void reportThreadPoolConfigParameter(ThreadPoolConfigEntity threadPoolConfigEntity) {
        String cacheKey = String.join("_", RegistryEnumVO.THREAD_POOL_CONFIG_PARAMETER_KEY.getKey(), threadPoolConfigEntity.getApplicationName(), threadPoolConfigEntity.getThreadPoolName());
        RBucket<Object> bucket = redissonClient.getBucket(cacheKey);
        // 默认存储30天
        bucket.set(threadPoolConfigEntity, Duration.ofDays(30));
    }
}
