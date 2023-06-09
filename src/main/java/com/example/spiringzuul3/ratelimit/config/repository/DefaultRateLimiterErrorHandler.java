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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liel Chayoun
 */
public class DefaultRateLimiterErrorHandler implements RateLimiterErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultRateLimiterErrorHandler.class);

    @Override
    public void handleSaveError(String key, Exception e) {
        log.error("Failed saving rate for " + key + ", returning unsaved rate", e);
    }

    @Override
    public void handleFetchError(String key, Exception e) {
        log.error("Failed retrieving rate for " + key + ", will create new rate", e);
    }

    @Override
    public void handleError(String msg, Exception e) {
        log.error(msg, e);
    }
}
