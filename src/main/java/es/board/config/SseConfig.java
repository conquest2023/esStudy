package es.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


@Configuration
public class SseConfig {

    @Bean(name = "sseHeartbeatExecutor")
    public ScheduledExecutorService sseHeartbeatExecutor() {
        return Executors.newScheduledThreadPool(1); // 스레드 1개면 충분
    }
}

