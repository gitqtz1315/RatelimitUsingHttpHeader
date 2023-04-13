package com.example.spiringzuul3;

//import com.marcosbarbero.cloud.autoconfigure.zuul.ratelimit.config.RateLimitUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/greeting")
@Controller
@RequiredArgsConstructor
@Slf4j
public class GreetingController {

//    private final RateLimiter rateLimiter;
//    private final RateLimitUtils rateLimitUtils;





    @GetMapping("/simple")
    public ResponseEntity<String> getSimple() {

//        RateLimitType rateLimitType;
//


        return ResponseEntity.ok("Hi");
    }
}
