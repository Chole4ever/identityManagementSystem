package com.uav.node;

import java.net.*;

public class UDPServer {
    public static void main(String[] args) throws Exception {
        // 创建UDP Socket并绑定端口
        DatagramSocket socket = new DatagramSocket(44444);
        System.out.println("服务端监听中（端口 44444）...");

        // 接收缓冲区
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (true) {
            socket.receive(packet); // 阻塞等待数据
            String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.printf("收到来自 %s 的消息: %s\n", packet.getAddress(), msg);
        }
    }
}