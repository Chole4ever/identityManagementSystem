package com.uav.node.demos.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uav.node.demos.model.Credential;

import java.nio.charset.StandardCharsets;

public class JsonBytesConverter {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static <T> byte[] JsontoBytes(T obj) throws Exception {
        String json = mapper.writeValueAsString(obj);
        return json.getBytes(StandardCharsets.UTF_8);
    }

    // 字节数组 → JSON字符串 → 对象
    public static <T> T fromBytes(byte[] data, Class<T> type) throws Exception {
        String json = new String(data, StandardCharsets.UTF_8);
        return mapper.readValue(json, type);
    }

    public static void main(String[] args) throws Exception {
        Credential credential = new Credential();
        byte[] m = credential.toJson().getBytes();
        Credential credential1 = fromBytes(m,Credential.class);
        System.out.println(credential);
        System.out.println(credential1);
    }

}
