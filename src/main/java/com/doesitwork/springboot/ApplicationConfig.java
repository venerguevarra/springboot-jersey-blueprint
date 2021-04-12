package com.doesitwork.springboot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

import lombok.Getter;
import lombok.Setter;

@Configuration
@EnableConfigurationProperties
@Getter
@Setter
public class ApplicationConfig extends AsyncConfigurerSupport {

    @Value("${applicationConfig.version}")
    private String version;

    @Value("${applicationConfig.environment}")
    private String environment;

    @Value("${applicationConfig.applicationName}")
    private String applicationName;

}
