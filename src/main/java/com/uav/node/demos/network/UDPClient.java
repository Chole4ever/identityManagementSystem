package com.uav.node.demos.network;

import com.uav.node.demos.config.GlobalConfig;
import com.uav.node.demos.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.List;

@Component
public class UDPClient {
    @Qualifier("getConfig")
    @Autowired
    GlobalConfig config;
    public void Broadcast(Message message) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);
        InetAddress address = InetAddress.getByName("255.255.255.255");
        byte[] data = message.toByteArray();

        DatagramPacket packet1 = new DatagramPacket(
                data, data.length, address, 44444);
        DatagramPacket packet2 = new DatagramPacket(
                data, data.length, address, 44445);
        DatagramPacket packet3 = new DatagramPacket(
                data, data.length, address, 44446);

        socket.send(packet1);
        socket.send(packet2);
        socket.send(packet3);

        socket.close();
    }

    public void send(Message message, int toId) throws IOException {
        DatagramSocket socket = new DatagramSocket();

        List<String> ip = config.getPeerIps();
        List<Integer> peerPorts = config.getPeerudpPorts();
        String host = ip.get(toId);
        int port =peerPorts.get(toId);

        InetAddress address = new InetSocketAddress(host,port).getAddress();
        byte[] data = message.toByteArray();

        DatagramPacket packet = new DatagramPacket(
                data, data.length, address, config.getPeerudpPorts().get(toId));

        socket.send(packet);
        socket.close();
    }
}
