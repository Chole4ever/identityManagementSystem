package com.example.uav.config;

import lombok.Data;
import lombok.ToString;
import org.fisco.bcos.sdk.v3.config.model.AmopTopic;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@ToString
@Component
@ConfigurationProperties
@PropertySource(value = "classpath:fisco-config.properties", ignoreResourceNotFound = true, encoding = "UTF-8")
public class BcosConfig {
    private Map<String, Object> cryptoMaterial;
    public Map<String, List<String>> network;
    public List<AmopTopic> amop;
    public Map<String, Object> account;
    public Map<String, Object> threadPool;
}
