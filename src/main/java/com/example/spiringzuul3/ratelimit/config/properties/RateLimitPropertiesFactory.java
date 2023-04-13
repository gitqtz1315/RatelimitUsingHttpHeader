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

package com.example.spiringzuul3.ratelimit.config.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

@Data
@Slf4j
public class RateLimitPropertiesFactory {

    RateLimitProperties[] RotationRateLimitProperties = new RateLimitProperties[2];

    final RateLimitProperties rateLimitProperties;
    int index = 0;

    public RateLimitPropertiesFactory(final RateLimitProperties rateLimitProperties) {

        this.rateLimitProperties = rateLimitProperties;
        init(this.rateLimitProperties);
    }

    public void init(RateLimitProperties rateLimitProperties) {
        RotationRateLimitProperties[0] = SerializationUtils.clone(rateLimitProperties);
        RotationRateLimitProperties[1] = SerializationUtils.clone(rateLimitProperties);
    }

    public synchronized void swapRateLimitProperties(RateLimitProperties toBeRateLimitProperties) {
        int tmpIndex = index ^ 1;
        RotationRateLimitProperties[tmpIndex] = SerializationUtils.clone(toBeRateLimitProperties);
        index = tmpIndex;
        log.info(RotationRateLimitProperties[index].toString());
    }

    public RateLimitProperties getRateLimitProperties() {
//        if()

        return RotationRateLimitProperties[index];
    }
}
