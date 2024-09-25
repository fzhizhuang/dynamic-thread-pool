package love.xiaozhi.trigger.controller;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import love.xiaozhi.domain.admin.model.entity.ThreadPoolConfigEntity;
import love.xiaozhi.domain.admin.service.IThreadPoolService;
import love.xiaozhi.trigger.dto.UpdateThreadPoolDTO;
import love.xiaozhi.types.enums.ErrorCode;
import love.xiaozhi.types.res.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态线程池控制器
 *
 * @author Jason Zong
 * @since 2024/9/9
 */
@Slf4j
@RestController
@CrossOrigin("*")
@RequestMapping("/api/tp")
public class DynamicThreadPoolController {

    @Resource
    private IThreadPoolService threadPoolService;

    @GetMapping("/list")
    public ApiResponse<List<ThreadPoolConfigEntity>> listThreadPoolConfigs() {
        List<ThreadPoolConfigEntity> threadPoolConfigEntities = threadPoolService.listThreadPoolConfigs();
        return ApiResponse.ok(threadPoolConfigEntities);
    }

    @GetMapping("/query")
    public ApiResponse<ThreadPoolConfigEntity> queryThreadPoolConfig(@RequestParam("applicationName") String applicationName, @RequestParam("threadPoolName") String threadPoolName) {
        ThreadPoolConfigEntity threadPoolConfigEntity = threadPoolService.queryThreadPoolConfig(applicationName, threadPoolName);
        return ApiResponse.ok(threadPoolConfigEntity);
    }


    @PostMapping("/update")
    public ApiResponse<Void> updateThreadPoolConfig(@RequestBody @Valid UpdateThreadPoolDTO updateThreadPoolDTO) {
        try {
            log.info("修改线程池配置开始 applicationName:{} threadPoolName:{} corePoolSize:{} maxPoolSize:{}", updateThreadPoolDTO.getApplicationName(), updateThreadPoolDTO.getThreadPoolName(), updateThreadPoolDTO.getCorePoolSize(), updateThreadPoolDTO.getMaxPoolSize());
            threadPoolService.updateThreadPoolConfig(updateThreadPoolDTO);
            log.info("修改线程池配置完成");
            return ApiResponse.ok();
        } catch (Exception e) {
            log.info("修改线程池异常,{}", JSON.toJSONString(updateThreadPoolDTO), e);
            return ApiResponse.fail(ErrorCode.UN_ERROR);
        }
    }

}
