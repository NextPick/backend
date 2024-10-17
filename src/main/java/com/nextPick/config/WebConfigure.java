package com.nextPick.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration		// 자동 호출되어 설정됨
public class WebConfigure implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // 모든 client 접속 허가
        registry.addMapping("/**").allowedOrigins("*");
    }
}

