package com.example.event_driven_practice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    @Bean("applicationEventMulticaster")    // 반드시 AbstractApplicationContext 에서 지정한 스프링 빈 이름으로 설정
    public ApplicationEventMulticaster applicationEventMulticaster(TaskExecutor asyncEventTaskExecutor) {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(asyncEventTaskExecutor);   // 스레드 풀 설정
        return eventMulticaster;
    }

    @Bean
    public TaskExecutor asyncEventTaskExecutor() {
        ThreadPoolTaskExecutor asyncEventTaskExecutor = new ThreadPoolTaskExecutor();
        // 스레드 풀의 개수는 최대 10개까지 늘어남
        asyncEventTaskExecutor.setMaxPoolSize(10);
        // 스레드 풀의 이름은 eventExecutor- 로 시작함
        asyncEventTaskExecutor.setThreadNamePrefix("eventExecutor-");
        // 컨테이너가 모든 속성값을 적용한 후 initialize() 호출
        asyncEventTaskExecutor.afterPropertiesSet();

        return asyncEventTaskExecutor;
    }
}
