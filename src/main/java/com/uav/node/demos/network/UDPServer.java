package com.uav.node.demos.network;

import com.uav.node.demos.config.GlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

@Component
public class UDPServer {
    private final MessageCallback callback;

    public UDPServer(MessageCallback callback) {
        this.callback = callback;
    }
    Logger logger = LoggerFactory.getLogger(UDPServer.class);

    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    @PostConstruct
    public void startServer() throws SocketException {

        DatagramSocket broadSocket ;
        DatagramSocket p2pSocket ;
        try {
            broadSocket = new DatagramSocket(config.getBroadcastPort());
            p2pSocket = new DatagramSocket(config.getPeerudpPorts().get(config.getOwnerId()));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        logger.info("node "+config.getOwnerId()+" 服务端监听中（广播端口 {}}）...",config.getBroadcastPort());
        logger.info("node "+config.getOwnerId()+" 服务端监听中（udp接收端口 {}）...",config.getPeerudpPorts().get(config.getOwnerId()));

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        DatagramSocket finalSocket = broadSocket;

        DatagramSocket finalSocket2 = p2pSocket;

        executorService.submit(() -> {
            byte[] buffer = new byte[32768];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    finalSocket.receive(packet); // 阻塞等待数据
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    int length = packet.getLength();
                    if (length > buffer.length) {
                        logger.error("Received packet exceeds buffer size: " + length);
                        // 可以选择丢弃或者分块处理
                        continue;
                    }
                    callback.onMessageReceived(msg, packet.getAddress());
                } catch (IOException e) {
                    logger.info(e.getMessage());
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        executorService.submit(() -> {
            byte[] buffer = new byte[32768];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    finalSocket2.receive(packet); // 阻塞等待数据
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    int length = packet.getLength();
                    if (length > buffer.length) {
                        logger.error("Received packet exceeds buffer size: " + length);
                        continue;
                    }

                    callback.onMessageReceived(msg, packet.getAddress());
                } catch (IOException e) {
                    logger.info(e.getMessage());
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
