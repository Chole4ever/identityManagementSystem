package com.uav.node.demos.config;

import com.uav.node.demos.model.Credential;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
public class AuthSession {
    private String counterpartyGroupGdid;
    private String counterpartyLeaderDid;
    private Credential gvc;
}
