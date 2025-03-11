package com.uav.node.demos.network.udp;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.service.TransportService;
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
    public void startServer() {

        DatagramSocket socket = null;
        DatagramSocket socket2 = null;
        try {
            socket = new DatagramSocket(44444);
            socket2 = new DatagramSocket(config.getPeerudpPorts().get(config.getOwnerId()));
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        logger.info("node "+config.getOwnerId()+" 服务端监听中（广播端口 44444）...");
        logger.info("node "+config.getOwnerId()+" 服务端监听中（udp接收端口 {}）...",config.getPeerudpPorts().get(config.getOwnerId()));

        // 创建一个单独的线程处理数据接收
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        DatagramSocket finalSocket = socket;

        DatagramSocket finalSocket2 = socket2;
        executorService.submit(() -> {
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    finalSocket.receive(packet); // 阻塞等待数据
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    // 调用回调方法处理接收到的消息
                    int length = packet.getLength();
                    if (length > buffer.length) {
                        logger.error("Received packet exceeds buffer size: " + length);
                        // 可以选择丢弃或者分块处理
                        continue;
                    }
                    callback.onMessageReceived(msg, packet.getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        executorService.submit(() -> {
            byte[] buffer = new byte[2048];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                try {
                    finalSocket2.receive(packet); // 阻塞等待数据
                    String msg = new String(packet.getData(), 0, packet.getLength());
                    int length = packet.getLength();
                    if (length > buffer.length) {
                        logger.error("Received packet exceeds buffer size: " + length);
                        // 可以选择丢弃或者分块处理
                        continue;
                    }
                    // 调用回调方法处理接收到的消息
                    callback.onMessageReceived(msg, packet.getAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
