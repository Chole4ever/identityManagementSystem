package com.uav.node.demos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DDO {
    @JsonProperty("id")  // 根据实际JSON字段名修改
    private String did;

    @JsonProperty("gdid") // 驼峰命名示例，按需调整
    private String gdid;

    @JsonProperty("publicKeys") // 字段名与JSON一致时可省略
    private String[] publicKeys;

    @JsonProperty("service")  // 示例: 字段在JSON中叫"service"
    private String[] serviceList;

    @JsonProperty("created")  // 无特殊需求可省略
    private int created;

    @JsonProperty("updated")
    private int updated;
}