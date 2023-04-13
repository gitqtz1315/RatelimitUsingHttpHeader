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

import com.example.spiringzuul3.ratelimit.config.properties.validators.Policies;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Marcos Barbero
 * @author Liel Chayoun
 */
//@Validated
@NoArgsConstructor
@Data
public class PolicyListProperties implements Serializable {

    public static final String POLICY_LIST = "zuul.ratelimit.policy-list";

    //@Valid
    //@NotNull
    //@Policies
    //@NestedConfigurationProperty
    @JsonProperty(POLICY_LIST)
    private Map<String, List<RateLimitProperties.Policy>> policyList = Maps.newHashMap();

    private String firstName;

}
