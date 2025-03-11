package com.uav.node.demos.model;

import lombok.Data;

@Data
public class DDO {
    private String did;
    private String gdid;
    private String[] publicKeys;
    private String[] serviceList;
    private int created;
    private int updated;
}