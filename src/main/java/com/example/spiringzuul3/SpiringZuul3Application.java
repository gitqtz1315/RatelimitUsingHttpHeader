package com.example.spiringzuul3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableZuulProxy
//@SpringCloudApplication
@EnableScheduling
public class SpiringZuul3Application {

    public static void main(String[] args) {
        SpringApplication.run(SpiringZuul3Application.class, args);
    }

}
