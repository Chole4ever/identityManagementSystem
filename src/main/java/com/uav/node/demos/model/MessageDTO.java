package com.uav.node.demos.model;

import lombok.Data;

import java.net.InetAddress;

@Data
public class MessageDTO {
    Message message;
    InetAddress inetAddress;
    int port;
    public MessageDTO(Message message,InetAddress inetAddress,int port)
    {
        this.message = message;
        this.inetAddress = inetAddress;
    }
}
