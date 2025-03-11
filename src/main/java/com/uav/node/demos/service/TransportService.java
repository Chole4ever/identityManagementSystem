package com.example.uav.service;

import com.example.uav.model.Message;
import com.example.uav.network.tcp.NettyClientPool;
import com.example.uav.network.udp.NettyUDPClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;



@Service
public class TransportService {

    @Value("${uav.peerAddress}")
    List<String> peerIps;
    @Value("${uav.memberIds}")
    List<Integer> memberIds;
    @Value("${uav.leaderIp}")
    String leaderIp;

    @Autowired
    NettyUDPClient nettyUDPClient;
    @Autowired
    NettyClientPool nettyClientPool;
    private static int listenPort = 999;
    private final HashMap<Integer,String> ipMaps = new HashMap<>();

    @PostConstruct
    public void init() {

        for(int i=0;i<peerIps.size();i++)
        {
            ipMaps.put(memberIds.get(i),peerIps.get(i));
        }
    }

//    public void SendTcpMessage(Message message,int memberId)
//    {
//        InetSocketAddress inetSocketAddress =
//                new InetSocketAddress(ipMaps.get(memberId),listenPort);
//        nettyClientPool.SendMessage(inetSocketAddress,message);
//    }
//
//    public void SendBroadcastMessage(Message message) throws Exception {
//        //nettyUDPClient.Broadcast(message);
//    }

}
