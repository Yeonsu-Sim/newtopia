package io.ssafy.p.i13c203.gameserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    /**
     * 외부 API 호출/AI 요약용 Executor
     */
    @Bean(name = "aiSummaryTaskExecutor")
    public ThreadPoolTaskExecutor aiSummaryTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("ai-summary-");
        executor.setCorePoolSize(8);     // 동시 10~20명 수준에서 충분
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(100);  // 대기 큐
        executor.setKeepAliveSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * Spring MVC 비동기 응답용 Executor (SSE 등)
     */
    @Bean(name = "mvcAsyncTaskExecutor")
    public ThreadPoolTaskExecutor mvcAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("mvc-async-");
        executor.setCorePoolSize(4);   // 코어 수 정도면 충분
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(30);
        executor.initialize();
        return executor;
    }
}
