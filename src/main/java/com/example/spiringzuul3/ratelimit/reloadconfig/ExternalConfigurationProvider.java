package com.example.spiringzuul3.ratelimit.reloadconfig;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ExternalConfigurationProvider {

    final ConfigurableApplicationContext ctx;

    ExternalConfigurationProvider(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
    }

    public ExternalConfiguration get() {
        return ctx.getBean(ExternalConfiguration.class);
    }
}