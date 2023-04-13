package com.example.spiringzuul3.ratelimit.listener;

import com.example.spiringzuul3.ratelimit.support.RateLimitExceededEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventHandler {

    @EventListener
    public void observe(RateLimitExceededEvent event) {
        log.error("RateLimitExceeded {} : {} : {}",
                event.getPolicy().getType().toString(), event.getPolicy().getLimit(), event.getPolicy().getRefreshInterval());
    }
}
