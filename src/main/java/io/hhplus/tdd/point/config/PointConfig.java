package io.hhplus.tdd.point.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class PointConfig {

    @Bean
    public ReentrantLock reentrantLock() {
        return new ReentrantLock();
    }
}
