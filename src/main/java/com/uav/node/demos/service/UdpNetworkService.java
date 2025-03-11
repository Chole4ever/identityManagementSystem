package com.uav.node.demos.service;

import com.uav.node.demos.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UdpNetworkService {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    Logger logger = LoggerFactory.getLogger(GDIDService.class);

    public void print()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 44444 广播端口监听中",id);
        logger.info("节点:{} {} udp接收端口监听中",id,config.getPeerudpPorts().get(id));
        logger.info("节点:{} 发送 GDIDGeneration 广播消息",id);
        logger.info("节点:{} 收到广播消息 {} from 节点 {}","command: Launch_GDID_Generation",id,id);
    }
    public void print2()
    {
        int id = config.getOwnerId();
        logger.info("节点:{} 发送 群组GDID注册广播消息",id);
        logger.info("节点:{} 收到广播消息 {} from 节点 {}","command: GDID_Generation_request",id,id);

    }

}
