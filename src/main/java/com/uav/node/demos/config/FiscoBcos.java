package com.uav.node.demos.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.v3.BcosSDK;
import org.fisco.bcos.sdk.v3.config.ConfigOption;
import org.fisco.bcos.sdk.v3.config.exceptions.ConfigException;
import org.fisco.bcos.sdk.v3.config.model.ConfigProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Slf4j
@Data
@Component
public class FiscoBcos {

    @Autowired
    BcosConfig bcosConfig;

    BcosSDK bcosSDK;

    @PostConstruct
    public void init() {
        ConfigProperty configProperty = loadProperty();
        ConfigOption configOption;
        try {
            configOption = new ConfigOption(configProperty);
        } catch (ConfigException e) {
            log.error("init error:" + e.toString());
            return ;
        }
        bcosSDK = new BcosSDK(configOption);
    }

    public ConfigProperty loadProperty() {
        ConfigProperty configProperty = new ConfigProperty();
        configProperty.setCryptoMaterial(bcosConfig.getCryptoMaterial());
        configProperty.setAccount(bcosConfig.getAccount());
        configProperty.setNetwork(new HashMap<String, Object>(){{
            put("peers", bcosConfig.getNetwork().get("peers"));
        }} );
        configProperty.setAmop(bcosConfig.getAmop());
        configProperty.setThreadPool(bcosConfig.getThreadPool());
        return configProperty;
    }
}


