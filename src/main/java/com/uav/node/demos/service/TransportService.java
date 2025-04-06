package com.uav.node.demos.service;


import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;

import com.uav.node.demos.network.UDPClient;
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

    public void sendUDPMessage(Message message, int toId) throws Exception {
        try{
            logger.info("node "+config.getOwnerId()+" send udp message to {}: {}",toId,message.toGood());
            udpClient.send(message,toId);
        }catch (Exception e)
        {
            logger.info("sendUDPMessage {}",e.getMessage());

        }
    }
    public void sendUDPMessage(Message message,String ip, int port) throws Exception{
        try {
            logger.info("node "+config.getOwnerId()+" send udp message to {}:{}: {}",ip,port,message.toGood());
            udpClient.send(message,ip,port);
        }catch (Exception e)
        {
            logger.info("sendUDPMessage {}",e.getMessage());
        }

    }
    public void sendBroadcastMessage(Message message) throws Exception {
        try {
            logger.info("node "+config.getOwnerId()+" send broadcast message: "+message.toGood());
            udpClient.Broadcast(message);
        }catch (Exception e)
        {
            logger.info("sendBroadcastMessage {}",e.getMessage());
        }
    }

}
