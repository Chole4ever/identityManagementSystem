package com.uav.node.demos.service;


import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
import com.uav.node.demos.network.tcp.NettyClientPool;
import com.uav.node.demos.network.udp.UDPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.util.HashMap;


@Service
public class TransportService {

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    @Autowired
    NettyClientPool nettyClientPool;
    @Autowired
    UDPClient udpClient;
    Logger logger = LoggerFactory.getLogger(TransportService.class);
    private static final HashMap<Integer,String> ipMaps = new HashMap<>();

    @PostConstruct
    public void init() {

        for(int i=0;i<config.getPeerIps().size();i++)
        {
            ipMaps.put(config.getPeerIds().get(i),config.getPeerIps().get(i));
        }
    }

    public void sendTcpMessage(Message message, int toId)
    {
        InetSocketAddress inetSocketAddress =
                new InetSocketAddress(ipMaps.get(toId),config.getPeerServerPorts().get(toId));

        nettyClientPool.SendMessage(inetSocketAddress,message);
    }
    public void sendUDPMessage(Message message, int toId) throws Exception {
        logger.info("node "+config.getOwnerId()+" send udp message: "+message.toGood());
        // nettyUDPClient.Broadcast(message);
        udpClient.send(message,toId);
    }
    public void sendBroadcastMessage(Message message) throws Exception {
        logger.info("node "+config.getOwnerId()+" send broadcast message: "+message.toGood());
       // nettyUDPClient.Broadcast(message);
        udpClient.Broadcast(message);
    }

}
