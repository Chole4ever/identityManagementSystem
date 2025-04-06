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
    String groupName;
    String did;
    String gdid;
    String gcsdid;
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
    int authIp;
    int authPort;

    @Bean
    public GlobalConfig getConfig()
    {
        GlobalConfig config = new GlobalConfig();
        config.setOwnerId(ownerId);
        config.setDid(did);
        config.setGdid(gdid);
        config.setLeaderId(leaderId);
        config.setPeerIds(peerIds);
        config.setBroadcastPort(broadcastPort);
        config.setPeerIps(peerIps);
        config.setPeerServerPorts(peerServerPorts);
        config.setDidRegistryContractAddress(didRegistryContractAddress);
        config.setGdidRegistryContractAddress(gdidRegistryContractAddress);
        config.setAuthIp(authIp);
        config.setAuthPort(authPort);
        return config;
    }
}

