package com.uav.node.demos.model;

import lombok.Data;

import java.util.List;

@Data
public class Node {
    private String did;
    private String gdid;
    private int isLeader;//0 no 1 yes
    private List<String> publicKeys;
    private List<String > serviceList;

}
