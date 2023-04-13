/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.spiringzuul3.ratelimit.support;

import com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties;
import com.example.spiringzuul3.ratelimit.filters.RateLimitPreFilter;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * Event raised when a rate limit exceeded.
 *
 * @author vasilaio
 */
public final class RateLimitExceededEvent extends ApplicationEvent {
    private static final long serialVersionUID = 5241485625003998587L;

    private final RateLimitProperties.Policy policy;
    private final String remoteAddress;

    public RateLimitExceededEvent(RateLimitPreFilter source, RateLimitProperties.Policy policy, String remoteAddress) {
        super(source);
        this.policy = Objects.requireNonNull(policy, "Policy should not be null.");
        this.remoteAddress = Objects.requireNonNull(remoteAddress, "RemoteAddress should not be null.");
    }

    /**
     * Return the {@link Policy} which raised the event.
     *
     * @return the {@link Policy}
     */
    public RateLimitProperties.Policy getPolicy() {
        return this.policy;
    }

    /**
     * Return the remote IP address.
     *
     * @return the remote IP address
     */
    public String getRemoteAddress() {
        return this.remoteAddress;
    }
}