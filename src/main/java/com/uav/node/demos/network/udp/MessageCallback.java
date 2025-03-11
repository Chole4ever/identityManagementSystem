package com.uav.node.demos.network.udp;

import java.net.InetAddress;

public interface MessageCallback {
    void onMessageReceived(String message, InetAddress address) throws Exception;
}