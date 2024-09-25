package love.xiaozhi.domain.admin.service;


import love.xiaozhi.domain.admin.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.trigger.dto.UpdateThreadPoolDTO;

import java.util.List;

/**
 * 线程池服务接口
 *
 * @author Jason Zong
 * @since 2024/9/9
 */
public interface IThreadPoolService {

    /**
     * 查询所有的线程池配置
     *
     * @return 线程池配置列表
     */
    List<ThreadPoolConfigEntity> listThreadPoolConfigs();

    /**
     * 查询线程池配置
     *
     * @param applicationName 应用名称
     * @param threadPoolName  线程池名称
     * @return 线程池配置
     */
    ThreadPoolConfigEntity queryThreadPoolConfig(String applicationName, String threadPoolName);

    /**
     * 更新线程池配置
     *
     * @param updateThreadPoolDTO 更新线程池配置DTO
     */
    void updateThreadPoolConfig(UpdateThreadPoolDTO updateThreadPoolDTO);
}
