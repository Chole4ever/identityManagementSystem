package com.uav.node.demos.config;


import com.uav.node.demos.model.GDDO;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "uav")
public class GlobalConfig {
    int ownerId;
    int leaderId;
    String gdid;
    List<Integer> peerIds;
    int broadcastPort;
    List<String> peerIps;
    List<Integer> peerudpPorts;
    List<Integer> peerServerPorts;
    String didRegistryContractAddress;
    String gdidRegistryContractAddress;
    List<String>didLists;
    int threshold;
    int count;
    GDDO gddo;



    @Bean
    public GlobalConfig getConfig()
    {
        GlobalConfig config = new GlobalConfig();
        config.setOwnerId(ownerId);
        config.setLeaderId(leaderId);
        config.setPeerIds(peerIds);
        config.setBroadcastPort(broadcastPort);
        config.setPeerIps(peerIps);
        config.setPeerServerPorts(peerServerPorts);
        config.setDidRegistryContractAddress(didRegistryContractAddress);
        config.setGdidRegistryContractAddress(gdidRegistryContractAddress);
        return config;
    }
}

