package com.uav.node.demos.model;

import lombok.Data;

@Data
public class GDDO {
    private String gdid;
    private String[] publicKeys;
    private String[] serviceList;
    private String[] didList;
}