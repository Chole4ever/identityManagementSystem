package com.uav.node;

import java.net.*;

public class UDPClient {
    public static void main(String[] args) throws Exception {
        // 创建UDP Socket
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true); // 关键：启用广播模式

        // 构建广播消息
        String message = "这是Java的广播测试消息";
        byte[] data = message.getBytes();

        // 使用回环广播地址（适用于单机测试）
        InetAddress address = InetAddress.getByName("255.255.255.255");
        // 也可以尝试用本地子网广播地址，如 192.168.1.255（需根据实际IP修改）

        // 发送数据包

        for (int i=0;i<10;i++)
        {
            DatagramPacket packet = new DatagramPacket(
                    data, data.length, address, 44444
            );
            socket.send(packet);
        }
        System.out.println("广播消息已发送");

        socket.close();
    }
}