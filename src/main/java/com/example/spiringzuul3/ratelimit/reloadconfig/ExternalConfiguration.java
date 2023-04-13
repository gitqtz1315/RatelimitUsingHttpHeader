package com.example.spiringzuul3.ratelimit.reloadconfig;


import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class ExternalConfiguration {

    String message;
}