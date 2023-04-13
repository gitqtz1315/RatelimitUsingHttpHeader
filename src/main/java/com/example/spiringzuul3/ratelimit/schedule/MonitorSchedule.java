package com.example.spiringzuul3.ratelimit.schedule;

import com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class MonitorSchedule {
    private final RateLimitProperties rateLimitProperties;

    @Scheduled(fixedDelay = 3000)
    public void fixedRateScheduler() {
        log.info("{}", rateLimitProperties.getPolicyList().toString());
    }
}
