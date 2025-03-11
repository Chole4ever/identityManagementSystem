package com.uav.node.demos.network;

import java.net.InetAddress;

public interface MessageCallback {
    void onMessageReceived(String message, InetAddress address) throws Exception;
}