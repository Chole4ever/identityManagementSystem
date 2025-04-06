package com.uav.node.demos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class GDDO {
    @JsonProperty("gdid") // 驼峰命名示例，按需调整
    private String gdid;

    @JsonProperty("publicKeys") // 字段名与JSON一致时可省略
    private byte[] publicKeys;

    @JsonProperty("service")  // 示例: 字段在JSON中叫"service"
    private String[] serviceList;

    @JsonProperty("didList")  //
    private List<String> didList;

    @JsonProperty("seq")  //
    private BigInteger seq;
    @JsonProperty("created")  // 无特殊需求可省略
    private int created;
    @JsonProperty("updated")
    private int updated;

    public static String[] splitDIDList(String input) {

        // Pattern to match each "did" value
        String pattern = "did:UAV:[0-9]+";

        // Create a list to store the extracted did values
        List<String> didList = new ArrayList<>();

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Create matcher object
        Matcher m = r.matcher(input);

        // Find all matches and add to list
        while (m.find()) {
            didList.add(m.group());
        }
        return (String[]) didList.toArray();
    }
}