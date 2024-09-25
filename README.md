<h3 align="center">动态线程池组件</h3>

## 介绍
线程池（Thread Pool），是一种基于池化思想管理线程的工具，用于降低资源消耗、提高响应速度、提高线程的管理性。
池化技术的引入，可以有效的减少线程频繁申请/销毁和调度所带来的额外开销。
<br/>
但在实际的工作中，线程池使用的场景非常多，但线程池的参数并不好一次就配置好，同时需要做监控处理，知道整个线程的消耗情况。
根据IO密集型，CPU密集型不通过的任务差异，做压测验证调整。所以有一款动态线程池是非常重要的。

### 使用
#### pom引入依赖
```xml
 <dependency>
    <groupId>love.xiaozhi</groupId>
    <artifactId>dynamic-thread-pool</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>      
```
#### 配置
```java
@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
public class ThreadPoolConfig {

    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties properties) {
        // 实例化策略
        RejectedExecutionHandler handler = switch (properties.getPolicy()) {
            case "DiscardPolicy" -> new ThreadPoolExecutor.DiscardPolicy();
            case "DiscardOldestPolicy" -> new ThreadPoolExecutor.DiscardOldestPolicy();
            case "CallerRunsPolicy" -> new ThreadPoolExecutor.CallerRunsPolicy();
            default -> new ThreadPoolExecutor.AbortPolicy();
        };

        // 创建线程池
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getBlockQueueSize()),
                Executors.defaultThreadFactory(),
                handler);
    }
    
}

```
```java
@Data
@ConfigurationProperties(prefix = "thread.pool.executor.config", ignoreInvalidFields = true)
public class ThreadPoolConfigProperties {

    /** 核心线程数 */
    private Integer corePoolSize = 20;
    /** 最大线程数 */
    private Integer maxPoolSize = 200;
    /** 最大等待时间 */
    private Long keepAliveTime = 10L;
    /** 最大队列数 */
    private Integer blockQueueSize = 5000;
    /*
     * AbortPolicy：丢弃任务并抛出RejectedExecutionException异常。
     * DiscardPolicy：直接丢弃任务，但是不会抛出异常
     * DiscardOldestPolicy：将最早进入队列的任务删除，之后再尝试加入队列的任务被拒绝
     * CallerRunsPolicy：如果任务添加线程池失败，那么主线程自己执行该任务
     * */
    private String policy = "AbortPolicy";

}

```
```yaml
# 线程池配置
thread:
  pool:
    executor:
      config:
        core-pool-size: 20
        max-pool-size: 50
        keep-alive-time: 5000
        block-queue-size: 5000
        policy: CallerRunsPolicy
        
# 动态线程池配置
dynamic:
  tp:
    enabled: true
    host: 127.0.0.1
    port: 6379
    password: 123456

# 应用名称
spring:
  application:
    name: tp-demo
```

## 许可证

根据 License 许可证分发。打开 [LICENSE](LICENSE) 查看更多内容。
