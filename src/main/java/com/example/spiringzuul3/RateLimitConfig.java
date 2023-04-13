package com.example.spiringzuul3;

import com.example.spiringzuul3.ratelimit.config.RateLimitKeyGenerator;
import com.example.spiringzuul3.ratelimit.config.RateLimitUtils;
import com.example.spiringzuul3.ratelimit.config.RateLimiter;
import com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties;
import com.example.spiringzuul3.ratelimit.config.properties.RateLimitPropertiesFactory;
import com.example.spiringzuul3.ratelimit.config.repository.DefaultRateLimiterErrorHandler;
import com.example.spiringzuul3.ratelimit.config.repository.RateLimiterErrorHandler;
import com.example.spiringzuul3.ratelimit.config.repository.bucket4j.Bucket4jHazelcastRateLimiter;
import com.example.spiringzuul3.ratelimit.filters.RateLimitPostFilter;
import com.example.spiringzuul3.ratelimit.filters.RateLimitPreFilter;
import com.example.spiringzuul3.ratelimit.support.DefaultRateLimitKeyGenerator;
import com.example.spiringzuul3.ratelimit.support.DefaultRateLimitUtils;
import com.example.spiringzuul3.ratelimit.support.StringToMatchTypeConverter;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.map.IMap;
import com.netflix.zuul.ZuulFilter;
import io.github.bucket4j.grid.GridBucketState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UrlPathHelper;

import static com.example.spiringzuul3.ratelimit.config.properties.RateLimitProperties.PREFIX;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = PREFIX, name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class RateLimitConfig {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();

    @Bean
    @ConfigurationPropertiesBinding
    public StringToMatchTypeConverter stringToMatchTypeConverter() {
        return new StringToMatchTypeConverter();
    }

    @Bean
    public StringToMatchTypeConverter stringToMatchTypeManuallyConverter() {
        return new StringToMatchTypeConverter();
    }

    @Bean
    @ConditionalOnMissingBean(RateLimiterErrorHandler.class)
    public RateLimiterErrorHandler rateLimiterErrorHandler() {
        return new DefaultRateLimiterErrorHandler();
    }

    @Bean
    public ZuulFilter rateLimiterPreFilter(final RateLimiter rateLimiter, final RateLimitProperties rateLimitProperties,
                                           final RouteLocator routeLocator, final RateLimitKeyGenerator rateLimitKeyGenerator,
                                           final RateLimitUtils rateLimitUtils, final ApplicationEventPublisher eventPublisher) {
        return new RateLimitPreFilter(rateLimitProperties, routeLocator, URL_PATH_HELPER, rateLimiter,
                rateLimitKeyGenerator, rateLimitUtils, eventPublisher);
    }

    @Bean
    public ZuulFilter rateLimiterPostFilter(final RateLimiter rateLimiter, final RateLimitProperties rateLimitProperties,
                                            final RouteLocator routeLocator, final RateLimitKeyGenerator rateLimitKeyGenerator,
                                            final RateLimitUtils rateLimitUtils) {
        return new RateLimitPostFilter(rateLimitProperties, routeLocator, URL_PATH_HELPER, rateLimiter,
                rateLimitKeyGenerator, rateLimitUtils);
    }

    @Bean
    @ConditionalOnMissingBean(RateLimitKeyGenerator.class)
    public RateLimitKeyGenerator ratelimitKeyGenerator(final RateLimitProperties properties,
                                                       final RateLimitUtils rateLimitUtils) {
        return new DefaultRateLimitKeyGenerator(properties, rateLimitUtils);
    }

    @Configuration
    @ConditionalOnMissingBean(RateLimitUtils.class)
    public static class RateLimitUtilsConfiguration {

        @Bean
        @ConditionalOnMissingClass("org.springframework.security.core.Authentication")
        public RateLimitUtils rateLimitUtils(final RateLimitProperties rateLimitProperties) {
            return new DefaultRateLimitUtils(rateLimitProperties);
        }
    }

    @Configuration
    @ConditionalOnMissingBean(RateLimiter.class)
    @ConditionalOnClass({Hazelcast.class, IMap.class})
    @ConditionalOnProperty(prefix = PREFIX, name = "repository", havingValue = "BUCKET4J_HAZELCAST")
    public static class Bucket4jHazelcastConfiguration {

        @Bean
        public RateLimiter bucket4jHazelcastRateLimiter(@Qualifier("RateLimit") final IMap<String, GridBucketState> rateLimit) {
            return new Bucket4jHazelcastRateLimiter(rateLimit);
        }
    }

    @Bean
    @Qualifier("RateLimit")
    public IMap<String, GridBucketState> rateLimit() {
        return Hazelcast.newHazelcastInstance().getMap("rateLimit");
    }

//    @Bean
//    public RateLimitPropertiesFactory rateLimitPropertiesFactory(final RateLimitProperties rateLimitProperties) {
//        RateLimitPropertiesFactory propertiesFactory = new RateLimitPropertiesFactory(rateLimitProperties);
//        return propertiesFactory;
//    }
}
