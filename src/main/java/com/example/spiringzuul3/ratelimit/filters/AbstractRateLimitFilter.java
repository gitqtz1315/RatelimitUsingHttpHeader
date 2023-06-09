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

package com.example.spiringzuul3.ratelimit.filters;

import com.example.spiringzuul3.ratelimit.config.RateLimitUtils;
import com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties;
import com.example.spiringzuul3.ratelimit.config.properties.RateLimitType;
import com.example.spiringzuul3.ratelimit.support.RateLimitExceededException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.spiringzuul3.ratelimit.support.RateLimitConstants.*;


/**
 * @author Marcos Barbero
 * @author Liel Chayoun
 */
abstract class AbstractRateLimitFilter extends ZuulFilter {

    protected final RateLimitProperties properties;
    protected final RateLimitUtils rateLimitUtils;

    private final RouteLocator routeLocator;
    private final UrlPathHelper urlPathHelper;

    AbstractRateLimitFilter(final RateLimitProperties properties, final RouteLocator routeLocator,
                            final UrlPathHelper urlPathHelper, final RateLimitUtils rateLimitUtils) {
        this.properties = properties;
        this.routeLocator = routeLocator;
        this.urlPathHelper = urlPathHelper;
        this.rateLimitUtils = rateLimitUtils;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        if (!properties.isEnabled()) {
            return false;
        }

        if (originIsOnDenyRequest(request)) {
            int responseStatusCode = properties.getDenyRequest().getResponseStatusCode();
            ctx.setResponseStatusCode(responseStatusCode);
            ctx.setSendZuulResponse(false);

            throw new RateLimitExceededException(HttpStatus.valueOf(responseStatusCode));
        }

        return !policy(route(request), request).isEmpty();
    }

    Route route(HttpServletRequest request) {
        Route route = (Route) RequestContext.getCurrentContext().get(CURRENT_REQUEST_ROUTE);
        //if (route != null) {
        //    return route;
        //}

        String requestURI = urlPathHelper.getPathWithinApplication(request);
        route = routeLocator.getMatchingRoute(requestURI);

        addObjectToCurrentRequestContext(CURRENT_REQUEST_ROUTE, route);

        return route;
    }

    @SuppressWarnings("unchecked")
    protected List<RateLimitProperties.Policy> policy(Route route, HttpServletRequest request) {
        List<RateLimitProperties.Policy> policies = (List<RateLimitProperties.Policy>) RequestContext.getCurrentContext().get(CURRENT_REQUEST_POLICY);
        if (policies != null) {
            return policies;
        }

        String routeId = route != null ? route.getId() : null;

        RequestContext.getCurrentContext().put(ALREADY_LIMITED, false);

        policies = properties.getPolicies(routeId).stream()
                .filter(policy -> applyPolicy(request, route, policy))
                .collect(Collectors.toList());

        addObjectToCurrentRequestContext(CURRENT_REQUEST_POLICY, policies);

        return policies;
    }

    private boolean originIsOnDenyRequest(HttpServletRequest request) {
        RateLimitProperties.DenyRequest denyRequest = properties.getDenyRequest();
        return denyRequest.getOrigins().stream()
                .anyMatch(origin -> RateLimitType.ORIGIN.apply(request, null, rateLimitUtils, origin));
    }

    private void addObjectToCurrentRequestContext(String key, Object object) {
        if (object != null) {
            RequestContext.getCurrentContext().put(key, object);
        }
    }

    private boolean applyPolicy(HttpServletRequest request, Route route, RateLimitProperties.Policy policy) {
        List<RateLimitProperties.Policy.MatchType> types = policy.getType();
        boolean isAlreadyLimited = (boolean) RequestContext.getCurrentContext().get(ALREADY_LIMITED);
        if (policy.isBreakOnMatch() && !isAlreadyLimited && types.stream().allMatch(type -> type.apply(request, route, rateLimitUtils))) {
            RequestContext.getCurrentContext().put(ALREADY_LIMITED, true);
        }
        return (types.isEmpty() || types.stream().allMatch(type -> type.apply(request, route, rateLimitUtils))) && !isAlreadyLimited;
    }
}
