package com.example.uav.model;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.util.List;

@Data
public class Node {
    private String did;
    private String gdid;
    private boolean isLeader = false;
    private List<String> publicKeys;
    private List<String > serviceList;

}
