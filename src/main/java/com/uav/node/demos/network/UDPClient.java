package com.uav.node.demos.network;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;

@Component
public class UDPClient {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;

    Logger logger = LoggerFactory.getLogger(UDPClient.class);
    public void Broadcast(Message message) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        InetAddress address = InetAddress.getByName("255.255.255.255");
        byte[] data = message.toByteArray();

        DatagramPacket packet1 = new DatagramPacket(
                data, data.length, address, 54444);
        DatagramPacket packet2 = new DatagramPacket(
                data, data.length, address, 54445);
        DatagramPacket packet3 = new DatagramPacket(
                data, data.length, address, 54446);
        DatagramPacket packet4 = new DatagramPacket(
                data, data.length, address, 54447);
        DatagramPacket packet5 = new DatagramPacket(
                data, data.length, address, 54448);
        socket.send(packet1);
        socket.send(packet2);
        socket.send(packet3);
        socket.send(packet4);
        socket.send(packet5);

        socket.close();
    }

    public void send(Message message, int toId) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        List<String> ip = config.getPeerIps();
        List<Integer> peerPorts = config.getPeerudpPorts();
        String host = ip.get(toId);
        int port = peerPorts.get(toId);

        InetAddress address = new InetSocketAddress(host,port).getAddress();

        byte[] data = message.toByteArray();
        DatagramPacket packet = new DatagramPacket(
                data, data.length, address, config.getPeerudpPorts().get(toId));

        socket.send(packet);
        socket.close();
    }
    public void send(Message message, String ip,int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        InetAddress address = new InetSocketAddress(ip,port).getAddress();

        byte[] data = message.toByteArray();
        DatagramPacket packet = new DatagramPacket(
                data, data.length, address, port);

        socket.send(packet);
        socket.close();
    }
}
