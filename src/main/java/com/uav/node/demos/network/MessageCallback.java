package com.uav.node.demos.network;

import java.net.InetAddress;

public interface MessageCallback {
    void onMessageReceived(byte[] m, InetAddress address,int port) throws Exception;
}