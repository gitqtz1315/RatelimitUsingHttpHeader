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

package com.example.spiringzuul3.ratelimit.config.repository;

import com.example.spiringzuul3.ratelimit.config.Rate;
import com.example.spiringzuul3.ratelimit.config.RateLimiter;
import com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties;

import java.time.Duration;

/**
 * Bucket4j rate limiter configuration.
 *
 * @author Liel Chayoun
 * @since 2018-04-06
 */
public abstract class AbstractCacheRateLimiter implements RateLimiter {

    @Override
    public synchronized Rate consume(RateLimitProperties.Policy policy, String key, Long requestTime) {
        final Duration refreshInterval = policy.getRefreshInterval();
        final Long quota = policy.getQuota() != null ? policy.getQuota().toMillis() : null;
        final Rate rate = new Rate(key, policy.getLimit(), quota, null, null);

        calcRemainingLimit(policy.getLimit(), refreshInterval, requestTime, key, rate);
        calcRemainingQuota(quota, refreshInterval, requestTime, key, rate);

        return rate;
    }

    protected abstract void calcRemainingLimit(Long limit, Duration refreshInterval, Long requestTime, String key, Rate rate);

    protected abstract void calcRemainingQuota(Long quota, Duration refreshInterval, Long requestTime, String key, Rate rate);
}
